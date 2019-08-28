package com.intendia.gwt.autorest.processor;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.intendia.gwt.autorest.client.AutoRestGwt;
import com.intendia.gwt.autorest.client.ResourceVisitor;
import com.intendia.gwt.autorest.client.ServiceFactory;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SupportedAnnotationTypes(AutoRestGwtProcessor.AUTO_REST_GWT)
@SupportedOptions({AutoRestGwtProcessor.DEBUG_OPTION})
public class AutoRestGwtProcessor extends AbstractProcessor {
    static final String AUTO_REST_GWT = "com.intendia.gwt.autorest.client.AutoRestGwt";
    static final String DEBUG_OPTION = "debug";

    private static class ServiceInfo {
        ClassName intfName;
        ClassName modelName;
        boolean rx;

        ServiceInfo(ClassName intfName, ClassName modelName, boolean rx) {
            this.intfName = intfName;
            this.modelName = modelName;
            this.rx = rx;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("intfName", intfName)
                    .add("modelName", modelName)
                    .add("rx", rx)
                    .toString();
        }
    }


    //@Override public Set<String> getSupportedOptions() { return singleton("debug"); }

    //@Override public Set<String> getSupportedAnnotationTypes() { return singleton(AutoRestGwt); }

    @Override public SourceVersion getSupportedSourceVersion() { return SourceVersion.latestSupported(); }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) return true;
        Map<String, List<ServiceInfo>> pkgSvcList = new HashMap<>();
        Set<TypeName> factoryInterfaces = new HashSet<>();
        roundEnv.getElementsAnnotatedWith(AutoRestGwt.class).stream()
                .filter(e -> e.getKind().isInterface() && e instanceof TypeElement).map(e -> (TypeElement) e)
                .forEach(restService -> {
                    try {
                        //AutoRestGwt autoRestGwt = restService.getAnnotation(AutoRestGwt.class);
                        AnnotationMirror annMirror = getMirror(restService);
                        if (annMirror != null) {
                            Map<String, Object> annValues = getAnnValues(annMirror);
                            boolean rx = (Boolean) annValues.get("rx");
                            TypeMirror factory = (TypeMirror) annValues.get("factory");
                            TypeMirror factoryInterface = (TypeMirror) annValues.get("factoryInterface");
                            if (factoryInterface.getKind() == TypeKind.DECLARED)
                                factoryInterfaces.add(TypeName.get(factoryInterface));
                            AbstractRestGwtServiceBuilder builder = rx ? new RxGwtBuilder(processingEnv)
                                    : new CallbackGwtBuilder(processingEnv);
                            ClassName modelName = builder.buildRestService(restService, factory);
                            List<ServiceInfo> serviceInfos = pkgSvcList.computeIfAbsent(modelName.packageName(), k -> new ArrayList<>());
                            serviceInfos.add(new ServiceInfo(ClassName.get(restService), modelName, rx));
                        }
                    } catch (Exception e) {
                        // We don't allow exceptions of any kind to propagate to the compiler
                        error("uncaught exception processing rest service " + restService + ": " + e + "\n"
                                + Throwables.getStackTraceAsString(e));
                    }
                });

        /*
        if (roundEnv.processingOver() && !pkgSvcList.isEmpty()) {
            String pkg = pkgSvcList.keySet().toArray(new String[0])[0];
            try {
                buildFactory(pkg, pkgSvcList.get(pkg), factoryInterfaces);
            } catch (IOException e) {
                error("uncaught exception creating ServiceFactory for " + pkg + ": " + e + "\n"
                        + Throwables.getStackTraceAsString(e));
            } finally {
                pkgSvcList.clear();
            }
        }
        pkgSvcList.keySet().forEach(pkg -> {
            try {
                buildFactory(pkg, pkgSvcList.get(pkg), factoryInterfaces);
            } catch (IOException e) {
                error("uncaught exception creating ServiceFactory for " + pkg + ": " + e + "\n"
                        + Throwables.getStackTraceAsString(e));
            }
        });
         */
        return true;
    }

    private void buildFactory(String pkgName, Collection<ServiceInfo> services, Collection<TypeName> factoryInterfaces) throws IOException {
        List<ServiceInfo> callbacks = services.stream().filter(i -> !i.rx).collect(Collectors.toList());
        List<ServiceInfo> reactives = services.stream().filter(i -> i.rx).collect(Collectors.toList());
        if (callbacks.isEmpty()) return;

        ClassName modelName = ClassName.get(pkgName, "ServiceFactoryImpl");

        TypeSpec.Builder modelTypeBuilder = TypeSpec.classBuilder(modelName.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ServiceFactory.class)
                .addSuperinterfaces(factoryInterfaces);

        if (!processingEnv.getOptions().containsKey("skipGeneratedAnnotation")) {
            modelTypeBuilder.addAnnotation(AnnotationSpec.builder(Generated.class)
                    .addMember("value", "$S", AutoRestGwtProcessor.class.getCanonicalName())
                    .addMember("date", "$S", LocalDateTime.now().toString())
                    .build());
        }

        if (!callbacks.isEmpty()) {
            TypeVariableName serviceTypeVariable = TypeVariableName.get("S");
            TypeVariableName callbackTypeVariable = TypeVariableName.get("T");
            TypeName serviceType = ParameterizedTypeName.get(ClassName.get(Class.class), serviceTypeVariable);
            TypeName callbackType = ParameterizedTypeName.get(ClassName.get(Consumer.class), callbackTypeVariable);
            TypeName errorHandlerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), TypeName.get(Throwable.class));
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("create")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addTypeVariable(serviceTypeVariable)
                    .addTypeVariable(callbackTypeVariable)
                    .returns(serviceTypeVariable)
                    .addParameter(serviceType, "service")
                    .addParameter(TypeName.get(ResourceVisitor.Supplier.class), "parent")
                    .addParameter(callbackType, "onSuccess")
                    .addParameter(errorHandlerType, "onError");
            CodeBlock.Builder methodCode = CodeBlock.builder()
                    .beginControlFlow("switch (service.getSimpleName())");
            callbacks.forEach(svc -> {
                methodCode.addStatement("case $S: return ($L) new $L(parent, onSuccess, onError)",
                        svc.intfName.simpleName(), serviceTypeVariable.name, svc.modelName.simpleName());
            });
            methodCode.addStatement("default: throw new RuntimeException($S$L$S)",
                    "Service + ", " + service.getSimpleName() + ", " is not supported")
                    .endControlFlow();
            modelTypeBuilder.addMethod(methodBuilder
                    .addCode(methodCode.build())
                    .build());
        } else
            error("No callbacks");

        Filer filer = processingEnv.getFiler();
        JavaFile.Builder file = JavaFile.builder(pkgName, modelTypeBuilder.build());
        boolean skipJavaLangImports = processingEnv.getOptions().containsKey("skipJavaLangImports");
        file.indent(Strings.repeat(" ", 4))
                .skipJavaLangImports(skipJavaLangImports)
                .build()
                .writeTo(filer);
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }

    private static AnnotationMirror getMirror(Element target) {
        return target.getAnnotationMirrors().stream()
                .filter(m -> AutoRestGwtProcessor.AUTO_REST_GWT.contentEquals(
                        (((TypeElement)m.getAnnotationType().asElement()).getQualifiedName())))
                .findAny()
                .orElse(null);
    }

    private Map<String, Object> getAnnValues(AnnotationMirror annMirror) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elemValues = processingEnv.getElementUtils()
                .getElementValuesWithDefaults(annMirror);
        Map<String, Object> values = new HashMap<>();
        for (ExecutableElement el: elemValues.keySet())
            values.put(el.getSimpleName().toString(), elemValues.get(el).getValue());
        return values;
    }
}
