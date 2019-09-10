package com.intendia.gwt.autorest.example.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.intendia.gwt.autorest.example.client.AuthType;
import com.intendia.gwt.autorest.example.client.SwSessionInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.entity.ContentType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

// @WebServlet(name = "greeting-service", urlPatterns = "/example/api/*")
public class SessionServlet extends HttpServlet {
    private static final Logger L = Logger.getLogger(SessionServlet.class.getName());
    private static final String helloWorldJson = "[{\"greeting\":\"Hello World\"}]";

    private final ObjectMapper mapper;

    private final Map<String, SwSessionInfo> sessions = new HashMap<>();

    public SessionServlet() {
        super();
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getRequestURI();

        try {
            Object value = null;
            if (uri.endsWith("/login")) {
                L.info("Attempting to login");
                value = doLogin(req, resp);
            } else if (uri.endsWith("/find")) {
                value = new ArrayList<>(sessions.values());
            } else if (uri.endsWith("/current")) {
                L.info("validating current session");
                String authToken = req.getHeader("auth");
                if (Strings.isNullOrEmpty(authToken) || !sessions.containsKey(authToken))
                    abortRequest(resp, String.format("Token <%s> is invalid", authToken));
                value = sessions.get(authToken);
            } else if (uri.endsWith("/validate")) {
                SwSessionInfo sessionInfo = mapper.readValue(req.getInputStream(), SwSessionInfo.class);
                resp.setContentType(ContentType.TEXT_PLAIN.withCharset(UTF_8).toString());
                resp.getOutputStream().print(sessions.containsKey(sessionInfo.authToken)
                        ? "true" : "false");
            } else if (uri.endsWith("/weekend")) {
                value = ImmutableList.of("Saturday", "Sunday");
            } else if (uri.endsWith("/id")) {
                value = RandomUtils.nextLong(0, 999L);
            }

            if (value != null) {
                resp.setContentType(ContentType.APPLICATION_JSON.withCharset(UTF_8).toString());
                mapper.writeValue(resp.getOutputStream(), value);
            }
        } catch (Throwable e) {
            L.log(Level.SEVERE, "error processing request", e);
        }
    }

    private SwSessionInfo doLogin(HttpServletRequest req, HttpServletResponse resp) {
        AuthType authType = AuthType.fromString(req.getParameter("authType"));
        String user = req.getParameter("user");
        try {
            String password = IOUtils.toString(req.getInputStream(), UTF_8);
            if (Strings.isNullOrEmpty(password))
                abortRequest(resp, "Password must be set");
            SwSessionInfo sessionInfo = new SwSessionInfo();
            sessionInfo.authToken = RandomStringUtils.randomAlphanumeric(15);
            sessionInfo.userName = user;
            sessionInfo.userType = authType.name();
            sessions.put(sessionInfo.authToken, sessionInfo);
            return sessionInfo;
        } catch (IOException e) {
            L.log(Level.SEVERE, "Error logging in", e);
        }
        return null;
    }

    private static void abortRequest(HttpServletResponse response, String message) {
        try {
            response.sendError(INTERNAL_SERVER_ERROR.getStatusCode(), message);
        } catch (IOException e) {
            L.log(Level.SEVERE, "Error producing error", e);
        }
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        L.info("Void pong response...");
    }
}
