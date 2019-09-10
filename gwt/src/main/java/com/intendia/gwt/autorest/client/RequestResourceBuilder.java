package com.intendia.gwt.autorest.client;

import elemental2.dom.XMLHttpRequest;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("GwtInconsistentSerializableClass")
public class RequestResourceBuilder extends RequestBuilder {
    public static final Function<RequestResourceBuilder, XMLHttpRequest> DEFAULT_REQUEST_FACTORY = data -> {
        XMLHttpRequest xhr = new XMLHttpRequest(); xhr.open(data.method(), data.uri()); return xhr;
    };
    public static final BiFunction<Single<XMLHttpRequest>, RequestResourceBuilder, Single<XMLHttpRequest>> DEFAULT_REQUEST_TRANSFORMER = (xml, data) -> xml;
    public static final Function<XMLHttpRequest, RequestResponseException.FailedStatusCodeException> DEFAULT_UNEXPECTED_MAPPER = xhr -> new RequestResponseException.FailedStatusCodeException(xhr, xhr.status, xhr.statusText);

    private Function<RequestResourceBuilder, XMLHttpRequest> requestFactory = DEFAULT_REQUEST_FACTORY;
    private Function<XMLHttpRequest, RequestResponseException.FailedStatusCodeException> unexpectedMapper = DEFAULT_UNEXPECTED_MAPPER;
    private BiFunction<Single<XMLHttpRequest>, RequestResourceBuilder, Single<XMLHttpRequest>> requestTransformer = DEFAULT_REQUEST_TRANSFORMER;

    public RequestResourceBuilder requestFactory(Function<RequestResourceBuilder, XMLHttpRequest> fn) {
        this.requestFactory = fn; return this;
    }

    public RequestResourceBuilder unexpectedMapper(Function<XMLHttpRequest, RequestResponseException.FailedStatusCodeException> fn) {
        this.unexpectedMapper = fn; return this;
    }

    public RequestResourceBuilder requestTransformer(
            BiFunction<Single<XMLHttpRequest>, RequestResourceBuilder, Single<XMLHttpRequest>> fn) {
        this.requestTransformer = fn; return this;
    }

    @SuppressWarnings("unchecked")
    @Override public <T> T as(Class<? super T> container, Class<?> type) {
        if (Completable.class.equals(container)) return (T) request().toCompletable();
        if (Maybe.class.equals(container)) return (T) request().flatMapMaybe(ctx -> {
            @Nullable Object decode = decode(ctx);
            return decode == null ? Maybe.empty() : Maybe.just(decode);
        });
        if (Single.class.equals(container)) return (T) request().map(ctx -> {
            @Nullable Object decode = decode(ctx);
            return requireNonNull(decode, "null response forbidden, use Maybe instead");
        });
        if (Observable.class.equals(container)) return (T) request().toObservable().flatMapIterable(ctx -> {
            @Nullable Object[] decode = decode(ctx);
            return decode == null ? Collections.emptyList() : Arrays.asList(decode);
        });
        throw new UnsupportedOperationException("unsupported type " + container);
    }

    @Override
    public <T> void remoteCall(SuccessCallback<T> onSuccess, FailureCallback onError, Object context) {
        throw new UnsupportedOperationException("remoteCall is not supported in reactive version");
    }

    private Single<XMLHttpRequest> request() {
        return Single.<XMLHttpRequest>create(em -> {
            XMLHttpRequest xhr = requestFactory.apply(this);
            Map<String, String> headers = getHeaders(xhr);

            try {
                xhr.onreadystatechange = evt -> {
                    if (em.isDisposed()) return null;
                    if (xhr.readyState == XMLHttpRequest.DONE) {
                        if (isExpected(uri(), xhr.status)) em.onSuccess(xhr);
                        else em.tryOnError(unexpectedMapper.apply(xhr));
                    }
                    return null;
                };
                em.setCancellable(() -> {
                    if (xhr.readyState != XMLHttpRequest.DONE) xhr.abort();
                });

                sendRequest(xhr, headers);
            } catch (Throwable e) {
                em.tryOnError(new RequestResponseException(xhr, "", e));
            }
        }).compose(o -> requestTransformer.apply(o, this));
    }

}
