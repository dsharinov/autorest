package com.intendia.gwt.autorest.client;

import java.util.function.BiConsumer;

/**
 * @author DimaS
 */
@FunctionalInterface
public interface FailureCallback extends BiConsumer<Object, Throwable> {
    default
    void accept(Throwable e) {
        accept(null, e);
    }
}
