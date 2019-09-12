package com.intendia.gwt.autorest.example.client;

import com.intendia.gwt.autorest.client.AutoRestGwt;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;


@AutoRestGwt(rx = false)
@Path("/session")
public interface SessionResource {

    interface ResourceCommon {

        String TEXT_PLAIN_UTF8 = "text/plain;charset=UTF-8";
    }

    @PUT
    @Path("/ping")
    void ping();

    /**
     * User login with authentication type
     * @param authenticationType
     * @param userName
     * @param password
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("login")
    SwSessionInfo login(
            @QueryParam("authType") AuthType authenticationType,
            @QueryParam("user") String userName,
            String password,
            @QueryParam("agencyId") String agencyId);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("find")
    List<SwSessionInfo> find();

    default
    SwSessionInfo login(
            @QueryParam("authType") AuthType authenticationType,
            @QueryParam("user") String userName,
            String password) {
        return login(authenticationType, userName, password, null);
    }

    /**
     * User login with check-in information
     * @return {@link SwSessionInfo}
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("login/checkin")
    SwSessionInfo loginForCheckin(@QueryParam("data") String checkinData);

    /**
     * User logged in using "social login", we need to verify that it's valid
     * @return {@link SwSessionInfo}
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("login/social")
    SwSessionInfo loginSocial();

    /**
     * Retrieve current session info based on the previous authentication
     * @return {@link SwSessionInfo} or null
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("current")
    SwSessionInfo getSessionInfo();

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("validate")
    Boolean validateSessionInfo(SwSessionInfo sessionInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("id")
    double getId();

    /**
     * Logout current Seaware session
     */
    @POST
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("logout")
    void logoutSession();

    /**
     * Completely logout user
     */
    @POST
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("logout/user")
    void logoutUser();

    /**
     * Checks if current session is still valid and extends it.
     * If the session has already expired the returned object will have empty session id.
     * @return current {@link SwSessionInfo}
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("extension")
    SwSessionInfo extendSession();

    /*
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("pwdchange")
    SwSessionInfo changePassword(ProfileChangePwdData profileChangePwdData);
    */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("lock-expiration/res")
    ObjLockExpiration getResLockExpiration(@QueryParam("resId") Long resId);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("lock-expiration/res")
    ObjLockExpiration extendResLockExpiration(@QueryParam("resId") Long resId);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("permissions")
    Set<Permission> getPermissions(
            @QueryParam("authType") AuthType authenticationType);

    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("securitySettings")
    SecuritySettings getSecuritySettings();
    */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(ResourceCommon.TEXT_PLAIN_UTF8)
    @Path("securityReport")
    List<ValidationMessage> getSecurityReport();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("weekend")
    List<String> getWeekend();


    SwSessionInfo impersonateByEmail(String loginEmail);
}
