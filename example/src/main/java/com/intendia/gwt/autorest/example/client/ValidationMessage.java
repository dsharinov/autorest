package com.intendia.gwt.autorest.example.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ValidationMessage {

    public String entityName;
    public String message;

    @JsonIgnore
    public ValidationMessage(String entityName, String message) {
        this.entityName = entityName;
        this.message = message;
    }

    @Override @JsonIgnore
    public String toString() {
        return message;
    }
}
