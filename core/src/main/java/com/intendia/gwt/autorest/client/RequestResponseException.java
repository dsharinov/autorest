package com.intendia.gwt.autorest.client;

import elemental2.dom.XMLHttpRequest;

/* @Experimental */
public class RequestResponseException extends RuntimeException {
    private final XMLHttpRequest request;

    public RequestResponseException(XMLHttpRequest request, String msg, Throwable cause) {
        super(msg, cause);
        this.request = request;
    }

    public XMLHttpRequest getRequest() {
        return request;
    }

    public static class ResponseFormatException extends RequestResponseException {

        public ResponseFormatException(XMLHttpRequest request, String msg, Throwable cause) { super(request, msg, cause); }
    }

    public static class FailedStatusCodeException extends RequestResponseException {
        private final int status;

        public FailedStatusCodeException(XMLHttpRequest request, int status, String msg) {
            super(request, msg, null);
            this.status = status;
        }

        public int getStatusCode() { return status; }
    }
}
