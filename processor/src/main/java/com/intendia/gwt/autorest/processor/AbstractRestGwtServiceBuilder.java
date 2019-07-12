package com.intendia.gwt.autorest.processor;

import com.google.common.base.Strings;
import com.intendia.gwt.autorest.client.RestServiceModel;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.auto.common.MoreTypes.asElement;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.HEAD;
import static javax.ws.rs.HttpMethod.OPTIONS;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;

/**
 * @author DimaS
 */
abstract class AbstractRestGwtServiceBuilder {
    static final Set<String> HTTP_METHODS = Stream.of(GET, POST, PUT, DELETE, HEAD, OPTIONS).collect(toSet());
    static final String[] EMPTY = {};

    protected final ProcessingEnvironment processingEnv;

    AbstractRestGwtServiceBuilder(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public void buildRestService(TypeElement restService) throws IOException {
        String rsPath = restService.getAnnotation(Path.class).value();
        String[] produces = ofNullable(restService.getAnnotation(Produces.class)).map(Produces::value).orElse(EMPTY);
        String[] consumes = ofNullable(restService.getAnnotation(Consumes.class)).map(Consumes::value).orElse(EMPTY);

        ClassName rsName = ClassName.get(restService);
        log("rest service interface: " + rsName);

        ClassName modelName = ClassName.get(rsName.packageName(), rsName.simpleName() + suffix());
        log("rest service model: " + modelName);

        TypeSpec.Builder modelTypeBuilder = TypeSpec.classBuilder(modelName.simpleName())
                .addOriginatingElement(restService)
                .addModifiers(Modifier.PUBLIC)
                .superclass(RestServiceModel.class)
                .addSuperinterface(TypeName.get(restService.asType()));

        Set<String> methodImports = new HashSet<>();
        doBuildRestService(restService, rsPath, produces, consumes,
                rsName, modelName, modelTypeBuilder, method -> methodImport(methodImports, method));

        Filer filer = processingEnv.getFiler();
        JavaFile.Builder file = JavaFile.builder(rsName.packageName(), modelTypeBuilder.build());
        for (String staticImport : methodImports)
            file.addStaticImport(HttpMethod.class, staticImport);
        boolean skipJavaLangImports = processingEnv.getOptions().containsKey("skipJavaLangImports");
        file.indent(Strings.repeat(" ", 4))
                .skipJavaLangImports(skipJavaLangImports)
                .build()
                .writeTo(filer);
    }

    abstract protected String suffix();

    protected abstract void doBuildRestService(TypeElement restService, String rsPath,
                                               String[] produces, String[] consumes,
                                               ClassName rsName, ClassName modelName,
                                               TypeSpec.Builder modelTypeBuilder,
                                               Function<String, String> checkMethodName
                                               );

    private String methodImport(Set<String> methodImports, String method) {
        if (HTTP_METHODS.contains(method)) {
            methodImports.add(method); return method;
        } else {
            return "\"" + method + "\"";
        }
    }

    protected boolean isParam(VariableElement a) {
        return a.getAnnotation(CookieParam.class) != null
                || a.getAnnotation(FormParam.class) != null
                || a.getAnnotation(HeaderParam.class) != null
                || a.getAnnotation(MatrixParam.class) != null
                || a.getAnnotation(PathParam.class) != null
                || a.getAnnotation(QueryParam.class) != null;
    }

    protected Optional<? extends AnnotationMirror> isIncompatible(ExecutableElement method) {
        return method.getAnnotationMirrors().stream().filter(this::isIncompatible).findAny();
    }

    private boolean isIncompatible(AnnotationMirror a) {
        return a.getAnnotationType().toString().endsWith("GwtIncompatible");
    }

    protected void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    protected List<ExecutableElement> getServiceMethods(TypeElement restService) {
        return restService.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD && e instanceof ExecutableElement)
                .map(e -> (ExecutableElement) e)
                .filter(method -> !(method.getModifiers().contains(STATIC) || method.isDefault()))
                .collect(Collectors.toList());
    }

    protected boolean processIncompatible(TypeSpec.Builder modelTypeBuilder, ExecutableElement method, String methodName) {
        Optional<? extends AnnotationMirror> incompatible = isIncompatible(method);
        incompatible.ifPresent(annotationMirror -> modelTypeBuilder.addMethod(MethodSpec.overriding(method)
                .addAnnotation(AnnotationSpec.get(annotationMirror))
                .addStatement("throw new $T(\"$L\")", UnsupportedOperationException.class, methodName)
                .build()));
        return incompatible.isPresent();
    }

    protected void prepareCall(String[] produces, String[] consumes, Function<String, String> checkMethodName,
                               ExecutableElement method, CodeBlock.Builder builder) {
        // method type
        builder.add("method($L)", checkMethodName.apply(method.getAnnotationMirrors().stream()
                .map(a -> asElement(a.getAnnotationType()).getAnnotation(HttpMethod.class))
                .filter(Objects::nonNull).map(HttpMethod::value).findFirst().orElse(GET)));
        // resolve paths
        builder.add("\n.path($L)", Arrays
                .stream(ofNullable(method.getAnnotation(Path.class)).map(Path::value).orElse("").split("/"))
                .filter(s -> !s.isEmpty()).map(path -> !path.startsWith("{") ? "\"" + path + "\"" : method
                        .getParameters().stream()
                        .filter(a -> ofNullable(a.getAnnotation(PathParam.class)).map(PathParam::value)
                                .map(v -> path.equals("{" + v + "}")).orElse(false))
                        .findFirst().map(VariableElement::getSimpleName).map(Object::toString)
                        // next comment will produce a compilation error so the user get notified
                        .orElse("/* path param " + path + " does not match any argument! */"))
                .collect(Collectors.joining(", ")));
        // produces
        if (produces.length > 0)
            builder.add(".produces($L)", Arrays
                    .stream(ofNullable(method.getAnnotation(Produces.class)).map(Produces::value).orElse(produces))
                    .map(str -> "\"" + str + "\"").collect(Collectors.joining(", ")));
        // consumes
        if (consumes.length > 0)
            builder.add(".consumes($L)", Arrays
                    .stream(ofNullable(method.getAnnotation(Consumes.class)).map(Consumes::value).orElse(consumes))
                    .map(str -> "\"" + str + "\"").collect(Collectors.joining(", ")));
        // query params
        method.getParameters().stream().filter(p -> p.getAnnotation(QueryParam.class) != null).forEach(p ->
                builder.add(".param($S, $L)", p.getAnnotation(QueryParam.class).value(), p.getSimpleName()));
        // header params
        method.getParameters().stream().filter(p -> p.getAnnotation(HeaderParam.class) != null).forEach(p ->
                builder.add(".header($S, $L)", p.getAnnotation(HeaderParam.class).value(), p.getSimpleName()));
        // form params
        method.getParameters().stream().filter(p -> p.getAnnotation(FormParam.class) != null).forEach(p ->
                builder.add(".form($S, $L)", p.getAnnotation(FormParam.class).value(), p.getSimpleName()));
        // data
        method.getParameters().stream().filter(p -> !isParam(p)).findFirst()
                .ifPresent(data -> builder.add(".data($L)", data.getSimpleName()));
    }
}
