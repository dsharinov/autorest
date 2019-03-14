package com.intendia.gwt.autorest.example.client;

import java.util.List;

public class ValidationMessage {

    public enum MessageSource {
        CORE, CUSTOM
    }

    public enum MessageEntity {
        ROLE, USER_GROUP, SETTING
    }

    private String entityName;
    private String message;
    private List<String> wrongValues;
    private List<String> unusedValues;
    private MessageSource source;
    private MessageEntity entity;

    public ValidationMessage() {
    }

    public ValidationMessage(String entityName, String message, List<String> wrongValues, List<String> unusedValues,
                             MessageSource source, MessageEntity entity) {
        this.setEntityName(entityName);
        this.setMessage(message);
        this.setWrongValues(wrongValues);
        this.setUnusedValues(unusedValues);
        this.setSource(source);
        this.setEntity(entity);
    }

    public String getEntityName() {
        return entityName;
    }


    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getWrongValues() {
        return wrongValues;
    }

    public void setWrongValues(List<String> wrongValues) {
        this.wrongValues = wrongValues;
    }

    public List<String> getUnusedValues() {
        return unusedValues;
    }

    public void setUnusedValues(List<String> unusedValues) {
        this.unusedValues = unusedValues;
    }

    public MessageSource getSource() {
        return source;
    }

    public void setSource(MessageSource source) {
        this.source = source;
    }

    public MessageEntity getEntity() {
        return entity;
    }

    public void setEntity(MessageEntity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return message;
    }
}
