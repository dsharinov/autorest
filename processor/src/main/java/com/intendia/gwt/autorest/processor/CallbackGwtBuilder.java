package com.intendia.gwt.autorest.processor;

import com.google.auto.common.MoreTypes;
import com.intendia.gwt.autorest.client.ResourceVisitor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.auto.common.MoreTypes.asElement;
import static java.util.Optional.ofNullable;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.ws.rs.HttpMethod.GET;

/**
 * @author DimaS
 */
public class CallbackGwtBuilder extends AbstractRestGwtServiceBuilder {
    private static final String CALLBACK = "callback";
    private static final String ON_ERROR = "onError";

    CallbackGwtBuilder(ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    protected void doBuildRestService(TypeElement restService, String rsPath,
                                      String[] produces, String[] consumes,
                                      ClassName rsName, ClassName modelName,
                                      TypeSpec.Builder modelTypeBuilder,
                                      Function<String, String> checkMethodName) {

        TypeVariableName typeVariable = TypeVariableName.get("T");
        modelTypeBuilder.addTypeVariable(typeVariable);

        TypeName callbackType = ParameterizedTypeName.get(ClassName.get(Consumer.class), typeVariable);
        TypeName errorHandlerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), TypeName.get(ErrorInfo.class));
        modelTypeBuilder
                .addField(callbackType, CALLBACK, PRIVATE, FINAL)
                .addField(errorHandlerType, ON_ERROR, PRIVATE, FINAL)
                .build();

        modelTypeBuilder.addMethod(MethodSpec.constructorBuilder()
                //.addAnnotation(Inject.class)
                .addModifiers(PRIVATE)
                .addParameter(TypeName.get(ResourceVisitor.Supplier.class), "parent")
                .addParameter(TypeName.get(ResourceVisitor.Supplier.class), "parent")
                .addStatement("super(() -> $L.get().path($S))",
                        "parent", rsPath)
                .build());

        List<ExecutableElement> methods = getServiceMethods(restService);

        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();

            if (processIncompatible(modelTypeBuilder, method, methodName)) continue;

            CodeBlock.Builder builder = CodeBlock.builder().add("$[return ");
            {
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
                builder.add(".produces($L)", Arrays
                        .stream(ofNullable(method.getAnnotation(Produces.class)).map(Produces::value).orElse(produces))
                        .map(str -> "\"" + str + "\"").collect(Collectors.joining(", ")));
                // consumes
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
            builder.add(".as($T.class, $T.class);\n$]",
                    processingEnv.getTypeUtils().erasure(method.getReturnType()),
                    MoreTypes.asDeclared(method.getReturnType()).getTypeArguments().stream().findFirst()
                            .map(TypeName::get).orElse(TypeName.get(Void.class)));

            modelTypeBuilder.addMethod(MethodSpec.overriding(method).addCode(builder.build()).build());
        }
    }

}
