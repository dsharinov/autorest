package com.intendia.gwt.autorest.processor;

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
import javax.lang.model.type.NoType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @author DimaS
 */
public class CallbackGwtBuilder extends AbstractRestGwtServiceBuilder {
    private static final String ON_SUCCESS = "onSuccess";
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
        TypeName errorHandlerType = ParameterizedTypeName.get(ClassName.get(Consumer.class), TypeName.get(Throwable.class));
        modelTypeBuilder
                .addField(callbackType, ON_SUCCESS, PRIVATE, FINAL)
                .addField(errorHandlerType, ON_ERROR, PRIVATE, FINAL)
                .build();

        modelTypeBuilder.addMethod(MethodSpec.constructorBuilder()
                //.addAnnotation(Inject.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.get(ResourceVisitor.Supplier.class), "parent")
                .addParameter(callbackType, ON_SUCCESS)
                .addParameter(errorHandlerType, ON_ERROR)
                .addStatement("super(() -> $L.get().path($S))",
                        "parent", rsPath)
                .addStatement("this.$L = $L", ON_SUCCESS, ON_SUCCESS)
                .addStatement("this.$L = $L", ON_ERROR, ON_ERROR)
                .build());

        List<ExecutableElement> methods = getServiceMethods(restService);

        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();

            if (processIncompatible(modelTypeBuilder, method, methodName)) continue;

            CodeBlock.Builder builder = CodeBlock.builder(); //.add("$[return ");
            {
                prepareCall(produces, consumes, checkMethodName, method, builder);
            }
            builder.add(".remoteCall($L, $L);\n", ON_SUCCESS, ON_ERROR);
            builder.addStatement("return$L", method.getReturnType() instanceof NoType ? "" : " null");

            modelTypeBuilder.addMethod(MethodSpec.overriding(method).addCode(builder.build()).build());
        }
    }

    @Override
    protected String suffix() {
        return "_RestServiceProxy";
    }

}
