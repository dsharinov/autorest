package com.intendia.gwt.autorest.example.client;

import com.google.common.base.Objects;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Stores information about current Seaware session with ability to be read/written it as a cookie
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SwSessionInfo {

    public String authToken;
    public String userType;
    public String userName;
    public String displayName;
    public String sessionId;
    public long clientId;
    public long agentId;
    public long agencyId;
    public int inactivityTimeout; // in minutes
    public String currency;
    public String country;
    public String officeCode;
    //private Set<Permission> permissions;
    public Integer agentXDaysPayNow;
    public String tnsName;
    public String schemaName;

    public SwSessionInfo() {
    }

    @JsOverlay
    public final String getAuthToken() {
        return authToken;
    }

    @JsOverlay
    public final AuthType getUserType() {
        return AuthType.fromString(userType);
    }

    @JsOverlay
    public final boolean isAnonymous() {
        return userType == null || getUserType().isAnonymous();
    }

    @JsOverlay
    public final String getUserName() {
        return userName;
    }

    @JsOverlay
    public final String getDisplayName() {
        return displayName;
    }

    @JsOverlay
    public final String getSessionId() {
        return sessionId;
    }

    @JsOverlay
    public final Long getClientId() {
        return clientId;
    }

    @JsOverlay
    public final Long getAgentId() {
        return agentId;
    }

    @JsOverlay
    public final Long getAgencyId() {
        return agencyId;
    }

    @JsOverlay
    public final int getInactivityTimeout() {
        return inactivityTimeout;
    }

    @JsOverlay
    public final String getCurrency() {
        return currency;
    }

    @JsOverlay
    public final String getCountry() {
        return country;
    }

    @JsOverlay
    public final String getOfficeCode() {
        return officeCode;
    }

//    public Set<Permission> getPermissions() {
//        return permissions;
//    }

    @JsOverlay
    public final Integer getAgentXDaysPayNow() {
        return agentXDaysPayNow;
    }

    @JsOverlay
    public final String getTnsName() {
        return tnsName;
    }

    @JsOverlay
    public final String getSchemaName() {
        return schemaName;
    }

    @JsOverlay
    public final Long getCurrentUserId() {
        if (userType == null) {
            return 0L;
        }
        switch (getUserType()) {
            case CUSTOMER:
                return getClientId();
            case TRAVEL_AGENT:
                return getAgentId();
            case RES_AGENT:
                return 0L; // FIXME: not yet supported by server side
            case INTERNAL:
                return 0L; // FIXME: not yet supported by server side
            default:
                return 0L;
        }
    }

    @Override
    @JsOverlay
    public final int hashCode(){
    	return Objects.hashCode(authToken, sessionId);
    }

    @Override
    @JsOverlay
    public final boolean equals(Object object){
    	if (object instanceof SwSessionInfo) {
    		SwSessionInfo that = (SwSessionInfo) object;
    		return Objects.equal(this.authToken, that.authToken) &&
    		        Objects.equal(this.sessionId, that.sessionId);
    	}
    	return false;
    }

}
