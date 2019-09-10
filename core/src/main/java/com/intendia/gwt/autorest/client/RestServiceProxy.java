package com.intendia.gwt.autorest.client;

import java.util.function.Consumer;

/**
 * @author DimaS
 */
public abstract class RestServiceProxy<T> extends RestServiceModel {
    protected final SuccessCallback<T> onSuccess;
    protected final FailureCallback onError;
    protected Consumer<Object> beforeCall;
    protected Object context;

    protected RestServiceProxy(ResourceVisitor.Supplier path,
                               SuccessCallback<T> onSuccess, FailureCallback onError) {
        super(path);
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    public RestServiceProxy<T> withOnBefore(Consumer<Object> beforeCall) {
        this.beforeCall = beforeCall;
        return this;
    }

    public Object getContext() {
        return context;
    }

    public RestServiceProxy<T> withContext(Object context) {
        this.context = context;
        return this;
    }

}
