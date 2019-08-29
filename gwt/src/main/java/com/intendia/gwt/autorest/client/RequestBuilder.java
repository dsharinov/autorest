package com.intendia.gwt.autorest.client;

import elemental2.core.Global;
import elemental2.dom.FormData;
import elemental2.dom.XMLHttpRequest;
import gwt.interop.utils.shared.collections.Array;
import jsinterop.base.Js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static elemental2.core.Global.encodeURIComponent;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

/**
 * @author DimaS
 */
abstract class RequestBuilder extends CollectorResourceVisitor {
    @Override protected String encodeComponent(String str) { return encodeURIComponent(str).replaceAll("%20", "+"); }

    static <T> T decode(XMLHttpRequest ctx) {
        try {
            String text = ctx.response.asString();
            return text == null || text.isEmpty() ? null : Js.cast(Global.JSON.parse(text));
        } catch (Throwable e) {
            throw new RequestResponseException.ResponseFormatException("Parsing response error", e);
        }
    }

    static <T> List<T> decodeAsList(XMLHttpRequest ctx) {
        Array<T> arr = decode(ctx);
        return arr != null ? arr.asList() : new ArrayList<>();
    }

    static <T> Set<T> decodeAsSet(XMLHttpRequest ctx) {
        Array<T> arr = decode(ctx);
        return arr != null ? new HashSet<>(arr.asList()) : new HashSet<>();
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
            if (!headers.containsKey(CONTENT_TYPE)) xhr.setRequestHeader(CONTENT_TYPE, APPLICATION_JSON);
            if (!headers.containsKey(ACCEPT)) xhr.setRequestHeader(ACCEPT, APPLICATION_JSON);
            if (data != null) xhr.send(Global.JSON.stringify(data));
            else xhr.send();
        }
    }
}
