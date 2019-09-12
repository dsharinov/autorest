package com.intendia.gwt.autorest.client;

import elemental2.core.Global;
import elemental2.dom.FormData;
import elemental2.dom.XMLHttpRequest;
import gwt.interop.utils.shared.collections.Array;
import jsinterop.base.Js;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static elemental2.core.Global.encodeURIComponent;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * @author DimaS
 */
abstract class RequestBuilder extends CollectorResourceVisitor {
    private static final String ACCEPT_HEADER = APPLICATION_JSON + ", " + TEXT_PLAIN;

    @Override protected String encodeComponent(String str) { return encodeURIComponent(str).replaceAll("%20", "+"); }

    <T> T decode(XMLHttpRequest ctx) {
        try {
            String text = ctx.response.asString();
            return text == null || text.isEmpty() ? null
                    : Js.cast(producesText() ? text : Global.JSON.parse(text));
        } catch (Throwable e) {
            throw new RequestResponseException.ResponseFormatException(ctx, "Parsing response error", e);
        }
    }

    <T> T decode(XMLHttpRequest ctx, Function<T, T> converter) {
        try {
            String text = ctx.response.asString();
            return text == null || text.isEmpty() ? null
                    : converter.apply(Js.cast(producesText() ? text : Global.JSON.parse(text)));
        } catch (Throwable e) {
            throw new RequestResponseException.ResponseFormatException(ctx, "Parsing response error", e);
        }
    }

    <T> List<T> decodeAsList(XMLHttpRequest ctx) {
        Array<T> arr = decode(ctx);
        return arr != null ? arr.asList() : new ArrayList<>();
    }

    <T> Set<T> decodeAsSet(XMLHttpRequest ctx) {
        return new HashSet<>(decodeAsList(ctx));
    }

    <T> List<T> decodeAsList(XMLHttpRequest ctx, Function<T, T> converter) {
        Array<T> arr = decode(ctx);
        return arr != null ? arr.stream().map(converter).collect(Collectors.toList())
                : new ArrayList<>();
    }

    <T> Set<T> decodeAsSet(XMLHttpRequest ctx, Function<T, T> converter) {
        return new HashSet<>(decodeAsList(ctx, converter));
    }

    protected Map<String, String> getHeaders(XMLHttpRequest xhr) {
        Map<String, String> headers = new HashMap<>();
        for (Param h : headerParams) headers.put(h.k, Objects.toString(h.v));
        for (Map.Entry<String, String> h : headers.entrySet()) xhr.setRequestHeader(h.getKey(), h.getValue());
        return headers;
    }

    protected void sendRequest(XMLHttpRequest xhr, Map<String, String> headers) {
        if (!formParams.isEmpty()) {
            xhr.setRequestHeader(CONTENT_TYPE, MULTIPART_FORM_DATA);
            FormData form = new FormData();
            formParams.forEach(p -> form.append(p.k, Objects.toString(p.v)));
            xhr.send(form);
        } else {
            boolean textContent = consumesText();
            if (!headers.containsKey(CONTENT_TYPE))
                xhr.setRequestHeader(CONTENT_TYPE, textContent ? TEXT_PLAIN : APPLICATION_JSON);
            if (!headers.containsKey(ACCEPT))
                xhr.setRequestHeader(ACCEPT, ACCEPT_HEADER);
            if (data != null) xhr.send(textContent ? data.toString() : Global.JSON.stringify(data));
            else xhr.send();
        }
    }

    private boolean consumesText() {
        return !hasMedia(consumes, APPLICATION_JSON) && hasMedia(consumes, TEXT_PLAIN);
    }

    private boolean producesText() {
        return !hasMedia(produces, APPLICATION_JSON) && hasMedia(produces, TEXT_PLAIN);
    }

    private static boolean hasMedia(String[] mediaTypes, String mediaType) {
        return Arrays.stream(mediaTypes)
                .anyMatch(s -> s.contains(mediaType));
    }

}
