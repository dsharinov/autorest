package com.intendia.gwt.autorest.example.client;

import com.google.common.base.Objects;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * Stores information about current Seaware session with ability to be read/written it as a cookie
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SwSessionInfo implements Serializable {

    public String authToken;
    public String userType;
    public String userName;
    public String displayName;
    public String sessionId;
    public Long clientId;
    public Long agentId;
    public Long agencyId;
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

    public String getAuthToken() {
        return authToken;
    }

    @JsOverlay
    public AuthType getUserType() {
        return AuthType.fromString(userType);
    }

    @JsOverlay
    public boolean isAnonymous() {
        return userType == null || getUserType().isAnonymous();
    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Long getClientId() {
        return clientId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public Long getAgencyId() {
        return agencyId;
    }

    public int getInactivityTimeout() {
        return inactivityTimeout;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    public String getOfficeCode() {
        return officeCode;
    }

//    public Set<Permission> getPermissions() {
//        return permissions;
//    }

    public Integer getAgentXDaysPayNow() {
        return agentXDaysPayNow;
    }

    public String getTnsName() {
        return tnsName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    @JsOverlay
    public Long getCurrentUserId() {
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
    public int hashCode(){
    	return Objects.hashCode(authToken, sessionId);
    }

    @Override
    @JsOverlay
    public boolean equals(Object object){
    	if (object instanceof SwSessionInfo) {
    		SwSessionInfo that = (SwSessionInfo) object;
    		return Objects.equal(this.authToken, that.authToken) &&
    		        Objects.equal(this.sessionId, that.sessionId);
    	}
    	return false;
    }

}
