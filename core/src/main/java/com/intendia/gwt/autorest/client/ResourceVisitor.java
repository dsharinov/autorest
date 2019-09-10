package com.intendia.gwt.autorest.client;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** Visit each resource gathering the metadata and end up calling {@link #as(Class, Class)}. */
public interface ResourceVisitor {

    /** Sets the http method. */
    ResourceVisitor method(String method);

    /** Append paths, or set if the path is absolute. */
    ResourceVisitor path(Object... paths);

    /** Sets the produced media-type. */
    ResourceVisitor produces(String... produces);

    /** Sets the consumed media-type. */
    ResourceVisitor consumes(String... consumes);

    /** Sets a query param. */
    ResourceVisitor param(String key, @Nullable Object value);

    /** Sets a header param. */
    ResourceVisitor header(String key, @Nullable Object value);

    /** Sets a from param. */
    ResourceVisitor form(String key, @Nullable Object value);

    /** Sets the content data. */
    ResourceVisitor data(Object data);

    /** Wrap the current resource state into a {@code container}. */
    <T> T as(Class<? super T> container, Class<?> type);

    <T> void remoteCall(SuccessCallback<T> onSuccess, FailureCallback onError, Object context);

    default
    <T> void remoteCallForList(SuccessCallback<List<T>> onSuccess, FailureCallback onError, Object context) {
        throw new RuntimeException("Not implemented");
    }

    default
    <T> void remoteCallForSet(SuccessCallback<Set<T>> onSuccess, FailureCallback onError, Object context) {
        throw new RuntimeException("Not implemented");
    }

    default
    <T> void remoteCall(SuccessCallback<T> onSuccess, FailureCallback onError, Object context, Function<T, T> converter) {
        throw new RuntimeException("Not implemented");
    }

    default
    <T> void remoteCallForList(SuccessCallback<List<T>> onSuccess, FailureCallback onError, Object context, Function<T, T> converter) {
        throw new RuntimeException("Not implemented");
    }

    default
    <T> void remoteCallForSet(SuccessCallback<Set<T>> onSuccess, FailureCallback onError, Object context, Function<T, T> converter) {
        throw new RuntimeException("Not implemented");
    }

    interface Supplier {
        ResourceVisitor get();
    }
}
