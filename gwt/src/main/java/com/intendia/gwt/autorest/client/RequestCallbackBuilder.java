package com.intendia.gwt.autorest.client;

import com.intendia.gwt.autorest.client.RequestResponseException.FailedStatusCodeException;
import elemental2.dom.XMLHttpRequest;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("GwtInconsistentSerializableClass")
public class RequestCallbackBuilder extends RequestBuilder {
    public static final Function<RequestCallbackBuilder, XMLHttpRequest> DEFAULT_REQUEST_FACTORY = data -> {
        XMLHttpRequest xhr = new XMLHttpRequest(); xhr.open(data.method(), data.uri()); return xhr;
    };
    public static final BiFunction<XMLHttpRequest, RequestCallbackBuilder, XMLHttpRequest> DEFAULT_REQUEST_TRANSFORMER = (xml, data) -> xml;
    public static final Function<XMLHttpRequest, FailedStatusCodeException> DEFAULT_UNEXPECTED_MAPPER = xhr -> new FailedStatusCodeException(xhr.status, xhr.statusText);

    private Function<RequestCallbackBuilder, XMLHttpRequest> requestFactory = DEFAULT_REQUEST_FACTORY;
    private Function<XMLHttpRequest, FailedStatusCodeException> unexpectedMapper = DEFAULT_UNEXPECTED_MAPPER;
    private BiFunction<XMLHttpRequest, RequestCallbackBuilder, XMLHttpRequest> requestTransformer = DEFAULT_REQUEST_TRANSFORMER;

    public RequestCallbackBuilder requestFactory(Function<RequestCallbackBuilder, XMLHttpRequest> fn) {
        this.requestFactory = fn; return this;
    }

    public RequestCallbackBuilder unexpectedMapper(Function<XMLHttpRequest, FailedStatusCodeException> fn) {
        this.unexpectedMapper = fn; return this;
    }

    public RequestCallbackBuilder requestTransformer(
            BiFunction<XMLHttpRequest, RequestCallbackBuilder, XMLHttpRequest> fn) {
        this.requestTransformer = fn; return this;
    }

    @Override public <T> T as(Class<? super T> container, Class<?> type) {
        throw new UnsupportedOperationException("Not supported. User remoteCall instead");
    }

    @Override
    public <T> void remoteCall(Consumer<T> onSuccess, Consumer<Throwable> onError) {
        request(ctx -> onSuccess.accept(decode(ctx)), (ctx, e) -> onError.accept(e));
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
            onError.accept(xhr, new RequestResponseException("", e));
        }
    }

}
