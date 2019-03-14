package com.intendia.gwt.autorest.example.client;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.util.EnumSet;
import java.util.Map;

public enum AuthType {
    //B2C
    INTERNAL("Internal", AuthorizedAppMode.B2C),
    NONE("None", AuthorizedAppMode.B2C),
    CUSTOMER("Customer", AuthorizedAppMode.B2C),
    //B2B
    TRAVEL_AGENT("Travel Agent", AuthorizedAppMode.B2B),
    //CC
    RES_AGENT("Res Agent", AuthorizedAppMode.CC);

    public static AuthType DEFAULT_ANONYMOUS_TYPE = AuthType.INTERNAL;

    public static EnumSet<AuthType> ANONYMOUS = EnumSet.of(INTERNAL, NONE);

    private static final Map<String, AuthType> authMap = Maps.newHashMap();

    static {
        for (AuthType a : AuthType.values())
            authMap.put(a.toString(), a);
    }

    private final String label;
    private final AuthorizedAppMode appMode;

    AuthType(String name, AuthorizedAppMode appMode) {
        this.label = name;
        this.appMode = appMode;
    }

    public String label() {
        return label;
    }

    public AuthorizedAppMode appMode() {
        return appMode;
    }

    public boolean isAnonymous() {
        return ANONYMOUS.contains(this);
    }

    public static AuthType fromString(String name) {
        AuthType type = authMap.get(name);
        return MoreObjects.firstNonNull(type, NONE);
    }
}
