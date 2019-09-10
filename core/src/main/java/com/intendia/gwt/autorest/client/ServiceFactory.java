package com.intendia.gwt.autorest.client;

/**
 * @author DimaS
 */
public interface ServiceFactory {
    <S, T> S create(Class<S> service, ResourceVisitor.Supplier parent,
                    SuccessCallback<T> onSuccess, FailureCallback onError);
}
