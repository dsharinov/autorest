package com.intendia.gwt.autorest.client;

import com.intendia.gwt.autorest.client.RequestResponseException.FailedStatusCodeException;
import elemental2.dom.XMLHttpRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("GwtInconsistentSerializableClass")
public class CallbackResourceBuilder extends RequestBuilder {
    public static final Function<CallbackResourceBuilder, XMLHttpRequest> DEFAULT_REQUEST_FACTORY = data -> {
        XMLHttpRequest xhr = new XMLHttpRequest(); xhr.open(data.method(), data.uri()); return xhr;
    };
    public static final BiFunction<XMLHttpRequest, CallbackResourceBuilder, XMLHttpRequest> DEFAULT_REQUEST_TRANSFORMER = (xml, data) -> xml;
    public static final Function<XMLHttpRequest, FailedStatusCodeException> DEFAULT_UNEXPECTED_MAPPER = xhr -> new FailedStatusCodeException(xhr, xhr.status, xhr.statusText);

    private Function<CallbackResourceBuilder, XMLHttpRequest> requestFactory = DEFAULT_REQUEST_FACTORY;
    private Function<XMLHttpRequest, FailedStatusCodeException> unexpectedMapper = DEFAULT_UNEXPECTED_MAPPER;
    private BiFunction<XMLHttpRequest, CallbackResourceBuilder, XMLHttpRequest> requestTransformer = DEFAULT_REQUEST_TRANSFORMER;

    public CallbackResourceBuilder requestFactory(Function<CallbackResourceBuilder, XMLHttpRequest> fn) {
        this.requestFactory = fn; return this;
    }

    public CallbackResourceBuilder unexpectedMapper(Function<XMLHttpRequest, FailedStatusCodeException> fn) {
        this.unexpectedMapper = fn; return this;
    }

    public CallbackResourceBuilder requestTransformer(
            BiFunction<XMLHttpRequest, CallbackResourceBuilder, XMLHttpRequest> fn) {
        this.requestTransformer = fn; return this;
    }

    @Override public <T> T as(Class<? super T> container, Class<?> type) {
        throw new UnsupportedOperationException("Not supported. User remoteCall instead");
    }

    @Override
    public <T> void remoteCall(SuccessCallback<T> onSuccess, FailureCallback onError, Object context) {
        request(ctx -> onSuccess.accept(context, decode(ctx)), (ctx, e) -> onError.accept(context, e));
    }

    @Override
    public <T> void remoteCallForList(SuccessCallback<List<T>> onSuccess, FailureCallback onError, Object context) {
        request(ctx -> onSuccess.accept(context, decodeAsList(ctx)), (ctx, e) -> onError.accept(context, e));
    }

    @Override
    public <T> void remoteCallForSet(SuccessCallback<Set<T>> onSuccess, FailureCallback onError, Object context) {
        request(ctx -> onSuccess.accept(context, decodeAsSet(ctx)), (ctx, e) -> onError.accept(context, e));
    }

    @Override
    public <T> void remoteCall(SuccessCallback<T> onSuccess, FailureCallback onError, Object context, Function<T, T> converter) {
        request(ctx -> onSuccess.accept(context, decode(ctx, converter)), (ctx, e) -> onError.accept(context, e));
    }

    @Override
    public <T> void remoteCallForList(SuccessCallback<List<T>> onSuccess, FailureCallback onError, Object context, Function<T, T> converter) {
        request(ctx -> onSuccess.accept(context, decodeAsList(ctx, converter)), (ctx, e) -> onError.accept(context, e));
    }

    @Override
    public <T> void remoteCallForSet(SuccessCallback<Set<T>> onSuccess, FailureCallback onError, Object context, Function<T, T> converter) {
        request(ctx -> onSuccess.accept(context, decodeAsSet(ctx, converter)), (ctx, e) -> onError.accept(context, e));
    }

    private void request(Consumer<XMLHttpRequest> onSuccess, BiConsumer<XMLHttpRequest, Throwable> onError) {
        XMLHttpRequest xhr = requestFactory.apply(this);
        Map<String, String> headers = getHeaders(xhr);

        try {
            xhr.onreadystatechange = evt -> {
                if (xhr.readyState == XMLHttpRequest.DONE) {
                    if (isExpected(uri(), xhr.status)) onSuccess.accept(xhr);
                    else onError.accept(xhr, unexpectedMapper.apply(xhr));
                }
                return null;
            };

            sendRequest(xhr, headers);
        } catch (Throwable e) {
            onError.accept(xhr, new RequestResponseException(xhr, "", e));
        }
    }

}
