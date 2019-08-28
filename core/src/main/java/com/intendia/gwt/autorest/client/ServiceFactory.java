package com.intendia.gwt.autorest.client;

import java.util.function.Consumer;

/**
 * @author DimaS
 */
public interface ServiceFactory {
    <S, T> S create(Class<S> service, ResourceVisitor.Supplier parent,
                    Consumer<T> onSuccess, Consumer<Throwable> onError);
}
