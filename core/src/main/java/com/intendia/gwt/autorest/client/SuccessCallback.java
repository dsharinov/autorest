package com.intendia.gwt.autorest.client;

import java.util.function.BiConsumer;

/**
 * @author DimaS
 */
@FunctionalInterface
public interface SuccessCallback<T> extends BiConsumer<Object, T> {
    default
    void accept(T data) {
        accept(null, data);
    }
}
