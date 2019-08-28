package com.intendia.gwt.autorest.example.client;

import com.intendia.gwt.autorest.client.CallbackServiceSupplier;
import com.intendia.gwt.autorest.client.ResourceVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author DimaS
 */
public class CallbackFactory {
    private static Map<Class<?>, CallbackServiceSupplier> proxies = new HashMap<>();

    public static void register(Class<?> clazz, CallbackServiceSupplier supplier) {
        proxies.put(clazz, supplier);
    }

    @SuppressWarnings("unchecked")
    public static <S, T> S create(Class<S> clazz, ResourceVisitor.Supplier parent,
                                                           Consumer<T> onSuccess, Consumer<Throwable> onError) {
        CallbackServiceSupplier service = proxies.get(clazz);
        if (service == null)
            throw new RuntimeException("Unknown service " + clazz.getSimpleName());
        return (S) service.create(parent, onSuccess, onError);
    }
}
