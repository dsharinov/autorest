package com.intendia.gwt.autorest.example.client;

import com.intendia.gwt.autorest.client.ResourceVisitor;
import com.intendia.gwt.autorest.client.ServiceFactory;

import javax.annotation.Generated;
import java.util.function.Consumer;

public class ServiceFactoryImpl implements ServiceFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <S, T> S create(Class<S> service, ResourceVisitor.Supplier parent, Consumer<T> onSuccess,
            Consumer<Throwable> onError) {
        switch (service.getSimpleName()) {
            case "SessionResource": return (S) new SessionResource_RestServiceProxy(parent, onSuccess, onError);
            default: throw new RuntimeException("Service + " + service.getSimpleName() + " is not supported");
        }
    }
}
