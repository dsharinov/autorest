package com.intendia.gwt.autorest.processor;

import com.google.common.base.Strings;
import com.intendia.gwt.autorest.client.FailureCallback;
import com.intendia.gwt.autorest.client.ResourceVisitor;
import com.intendia.gwt.autorest.client.RestServiceProxy;
import com.intendia.gwt.autorest.client.SuccessCallback;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author DimaS
 */
public class CallbackGwtBuilder extends AbstractRestGwtServiceBuilder {
    private static final String ON_SUCCESS = "onSuccess";
    private static final String ON_ERROR = "onError";
    private static final String BEFORE_CALL = "beforeCall";
    private static final String CONTEXT = "context";
    private static final String JS_ADJUSTER = "JS_ADJUSTER";
    private static final String JS_TYPE = "jsinterop.annotations.JsType";

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

        ClassName consumerClassName = ClassName.get(SuccessCallback.class);
        TypeName objectType = TypeName.get(Object.class);
        TypeName callbackType = ParameterizedTypeName.get(consumerClassName, typeVariable);
        TypeName errorHandlerType = TypeName.get(FailureCallback.class);
        TypeName jsAdjusterType = ParameterizedTypeName.get(ClassName.get(BiFunction.class),
                objectType, TypeName.get(String.class), objectType);

        TypeMirror listType = processingEnv.getTypeUtils().erasure(processingEnv.getElementUtils().getTypeElement(List.class.getCanonicalName()).asType());
        TypeMirror setType = processingEnv.getTypeUtils().erasure(processingEnv.getElementUtils().getTypeElement(Set.class.getCanonicalName()).asType());
        TypeMirror collType = processingEnv.getTypeUtils().erasure(processingEnv.getElementUtils().getTypeElement(Collection.class.getCanonicalName()).asType());

        AnnotationSpec supprWarnAnn = AnnotationSpec.builder(SuppressWarnings.class)
                .addMember("value", "$S", "unchecked")
                .build();

        boolean jsAdjusterPresent = false;

        modelTypeBuilder.addMethod(MethodSpec.constructorBuilder()
                //.addAnnotation(Inject.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.get(ResourceVisitor.Supplier.class), "parent")
                .addParameter(callbackType, ON_SUCCESS)
                .addParameter(errorHandlerType, ON_ERROR)
                .addStatement("super(() -> $L.get().path($S), $L, $L)",
                        "parent", rsPath, ON_SUCCESS, ON_ERROR)
                .build());

        List<ExecutableElement> methods = getServiceMethods(restService);

        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();

            if (processIncompatible(modelTypeBuilder, method, methodName)) continue;

            CodeBlock.Builder builder = CodeBlock.builder(); //.add("$[return ");
            {
                builder.beginControlFlow("if ($L != null)", BEFORE_CALL);
                builder.addStatement("$L.accept($L)", BEFORE_CALL, CONTEXT);
                builder.endControlFlow();
                prepareCall(produces, consumes, checkMethodName, method, builder);
            }
            String remoteCallName = "remoteCall";
            CodeBlock typeConversion = CodeBlock.builder().build();
            TypeMirror returnObjType = method.getReturnType();
            TypeMirror returnErasureType = processingEnv.getTypeUtils().erasure(returnObjType);
            if (processingEnv.getTypeUtils().isSubtype(returnErasureType, collType)
                    && !((DeclaredType) method.getReturnType()).getTypeArguments().isEmpty()) {
                returnObjType = ((DeclaredType) method.getReturnType()).getTypeArguments().get(0);
                if (processingEnv.getTypeUtils().isSameType(returnErasureType, listType)) {
                    remoteCallName = "remoteCallForList";
                    typeConversion = CodeBlock.of("($T<List<$T>>) ", consumerClassName, returnObjType);
                } else if (processingEnv.getTypeUtils().isSameType(returnErasureType, setType)) {
                    remoteCallName = "remoteCallForSet";
                    typeConversion = CodeBlock.of("($T<Set<$T>>) ", consumerClassName, returnObjType);
                }
            }

            String jsName = returnObjType.getKind().isPrimitive() ? null : getOriginalJsName(getJsName(processingEnv.getTypeUtils().asElement(returnObjType)));
            if (!Strings.isNullOrEmpty(jsName)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("JS Name for %s = %s", returnObjType, jsName));
                if (!jsAdjusterPresent) {
                    modelTypeBuilder.addField(jsAdjusterType, JS_ADJUSTER, PRIVATE, STATIC);
                    modelTypeBuilder.addMethod(MethodSpec.methodBuilder("registerJsAdjuster")
                            .addModifiers(PUBLIC, STATIC)
                            .addParameter(jsAdjusterType, "adjuster")
                            .addStatement("$L = adjuster", JS_ADJUSTER)
                            .build());
                    jsAdjusterPresent = true;
                }

                builder.add(".$L($L$L, $L, $L, $L != null ? o -> ($T) $L.apply(o, $S) : o -> o);\n", remoteCallName, typeConversion,
                        ON_SUCCESS, ON_ERROR, CONTEXT, JS_ADJUSTER,
                        returnObjType.equals(method.getReturnType()) ? typeVariable : returnObjType, JS_ADJUSTER, jsName);
            } else
                builder.add(".$L($L$L, $L, $L);\n", remoteCallName, typeConversion, ON_SUCCESS, ON_ERROR, CONTEXT);
            if (!(method.getReturnType() instanceof NoType))
                builder.addStatement("return null");

            MethodSpec.Builder methodBuilder = MethodSpec.overriding(method)
                    .addCode(builder.build());
            if (!typeConversion.isEmpty() || !Strings.isNullOrEmpty(jsName))
                methodBuilder.addAnnotation(supprWarnAnn);
            modelTypeBuilder.addMethod(methodBuilder.build());
        }
    }

    private static String getOriginalJsName(String jsName) {
        return jsName == null || Objects.equals(jsName, "Object") || Objects.equals(jsName, "<auto>")
                ? null : jsName;
    }

    private String getJsName(Element target) {
        if (target == null) return null;
        Map<String, Object> jsAnnVals = target.getAnnotationMirrors().stream()
                .filter(m -> JS_TYPE.contentEquals(
                    (((TypeElement) m.getAnnotationType().asElement()).getQualifiedName())))
                .findAny()
                .map(m -> AutoRestGwtProcessor.getAnnValues(processingEnv, m))
                .orElse(null);
        return jsAnnVals == null ? null : (String) jsAnnVals.get("name");
    }

    @Override
    protected String suffix() {
        return "_RestServiceProxy";
    }

    @Override
    protected CodeBlock newInstanceSupplier(ClassName className) {
        return CodeBlock.builder()
                .add("(p, s, e) -> new $L(p, s, e)", className.simpleName())
                .build();
    }

    @Override
    protected TypeName superclass() {
        return ParameterizedTypeName.get(ClassName.get(RestServiceProxy.class), TypeVariableName.get("T"));
    }

}
