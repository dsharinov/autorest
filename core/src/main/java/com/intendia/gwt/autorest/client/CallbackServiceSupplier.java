package com.intendia.gwt.autorest.client;

import java.util.function.Consumer;

/**
 * @author DimaS
 */
@FunctionalInterface
public interface CallbackServiceSupplier {
    RestServiceModel create(ResourceVisitor.Supplier parent, Consumer<?> onSuccess, Consumer<Throwable> onError);
}
