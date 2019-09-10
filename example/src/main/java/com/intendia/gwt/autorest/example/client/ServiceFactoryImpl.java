package com.intendia.gwt.autorest.example.client;

import com.intendia.gwt.autorest.client.FailureCallback;
import com.intendia.gwt.autorest.client.ResourceVisitor;
import com.intendia.gwt.autorest.client.ServiceFactory;
import com.intendia.gwt.autorest.client.SuccessCallback;

public class ServiceFactoryImpl implements ServiceFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <S, T> S create(Class<S> service, ResourceVisitor.Supplier parent, SuccessCallback<T> onSuccess,
                           FailureCallback onError) {
        switch (service.getSimpleName()) {
            case "SessionResource": return (S) new SessionResource_RestServiceProxy<T>(parent, (ctx, data) -> onSuccess.accept(data),
                    (ctx, e) -> onError.accept(e));
            default: throw new RuntimeException("Service + " + service.getSimpleName() + " is not supported");
        }
    }
}
