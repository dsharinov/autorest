package com.intendia.gwt.autorest.example.client;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Set;

/**
 * Stores information about current Seaware session with ability to be read/written it as a cookie
 */
public class SwSessionInfo implements Serializable {

    private String authToken;
    private AuthType userType;
    private String userName;
    private String displayName;
    private String sessionId;
    private Long clientId;
    private Long agentId;
    private Long agencyId;
    private int inactivityTimeout; // in minutes
    private String currency;
    private String country;
    private String officeCode;
    private Set<Permission> permissions;
    private Integer agentXDaysPayNow;
    private String tnsName;
    private String schemaName;

    SwSessionInfo() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public AuthType getUserType() {
        return userType;
    }

    public boolean isAnonymous() {
        return userType == null || userType.isAnonymous();
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

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public static SwSessionInfo notLoggedIn(AuthType userType, String userName) {
        SwSessionInfo swSession = new SwSessionInfo();
        swSession.userType = userType;
        swSession.userName = userName;
        return swSession;
    }

    public Integer getAgentXDaysPayNow() {
        return agentXDaysPayNow;
    }

    public String getTnsName() {
        return tnsName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public Long getCurrentUserId() {
        if (userType == null) {
            return 0L;
        }
        switch (userType) {
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
    public int hashCode(){
    	return Objects.hashCode(authToken, sessionId);
    }

    @Override
    public boolean equals(Object object){
    	if (object instanceof SwSessionInfo) {
    		SwSessionInfo that = (SwSessionInfo) object;
    		return Objects.equal(this.authToken, that.authToken) &&
    		        Objects.equal(this.sessionId, that.sessionId);
    	}
    	return false;
    }

}
