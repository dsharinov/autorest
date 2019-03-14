package com.intendia.gwt.autorest.processor;

import com.google.auto.common.MoreTypes;
import com.intendia.gwt.autorest.client.ResourceVisitor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.function.Function;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * @author DimaS
 */
public class RxGwtBuilder extends AbstractRestGwtServiceBuilder {
    public RxGwtBuilder(ProcessingEnvironment processingEnv) {super(processingEnv);}

    @Override
    protected void doBuildRestService(TypeElement restService, String rsPath,
                                      String[] produces, String[] consumes,
                                      ClassName rsName, ClassName modelName,
                                      TypeSpec.Builder modelTypeBuilder,
                                      Function<String, String> checkMethodName) {
        modelTypeBuilder.addMethod(MethodSpec.constructorBuilder()
                .addAnnotation(Inject.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.get(ResourceVisitor.Supplier.class), "parent", FINAL)
                .addStatement("super(new $T() { public $T get() { return $L.get().path($S); } })",
                        ResourceVisitor.Supplier.class, ResourceVisitor.class, "parent", rsPath)
                .build());

        List<ExecutableElement> methods = getServiceMethods(restService);

        for (ExecutableElement method : methods) {
            String methodName = method.getSimpleName().toString();

            if (processIncompatible(modelTypeBuilder, method, methodName)) continue;

            CodeBlock.Builder builder = CodeBlock.builder().add("$[return ");
            {
                prepareCall(produces, consumes, checkMethodName, method, builder);
            }
            builder.add(".as($T.class, $T.class);\n$]",
                    processingEnv.getTypeUtils().erasure(method.getReturnType()),
                    MoreTypes.asDeclared(method.getReturnType()).getTypeArguments().stream().findFirst()
                            .map(TypeName::get).orElse(TypeName.get(Void.class)));

            modelTypeBuilder.addMethod(MethodSpec.overriding(method).addCode(builder.build()).build());
        }
    }

    @Override
    protected String suffix() {
        return "_RestServiceModel";
    }
}
