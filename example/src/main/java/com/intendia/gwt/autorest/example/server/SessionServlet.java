package com.intendia.gwt.autorest.example.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.intendia.gwt.autorest.example.client.AuthType;
import com.intendia.gwt.autorest.example.client.SwSessionInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

// @WebServlet(name = "greeting-service", urlPatterns = "/example/api/*")
public class SessionServlet extends HttpServlet {
    private static final Logger L = Logger.getLogger(SessionServlet.class.getName());
    private static final String helloWorldJson = "[{\"greeting\":\"Hello World\"}]";

    private final ObjectMapper mapper;

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
        String uri = req.getRequestURI();
        L.info("Sending 'Hello World' in response of " + uri);
        try {
            String FOO_URI = "/example/api/observable/foo";
            if (uri.equals(FOO_URI)) {
                resp.getWriter().write("[{\"greeting\":\"/foo\"}]");
            } else if (uri.startsWith(FOO_URI)) {
                String x = uri.substring(FOO_URI.length()) + "?" + req.getQueryString();
                resp.getWriter().write("[{\"greeting\":\"/foo" + x + "\"}]");
            } else {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode helloJsonNode = mapper.readTree(helloWorldJson);
                mapper.writeValue(resp.getOutputStream(), helloJsonNode);
            }
        } catch (Throwable e) {
            L.log(Level.SEVERE, "error sending 'Hello World'", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getRequestURI();

        Object value = null;
        if (uri.endsWith("/login")) {
            L.info("Attempting to login");
            value = doLogin(req);
        }

        try {
            if (value != null) {
                resp.setContentType(ContentType.APPLICATION_JSON.withCharset(UTF_8).toString());
                mapper.writeValue(resp.getOutputStream(), value);
            }
        } catch (Throwable e) {
            L.log(Level.SEVERE, "error creating custom greeting", e);
        }
    }

    private SwSessionInfo doLogin(HttpServletRequest req) {
        AuthType authType = AuthType.fromString(req.getParameter("authType"));
        String user = req.getParameter("user");
        try {
            String password = IOUtils.toString(req.getInputStream(), UTF_8);
            if (Strings.isNullOrEmpty(password))
                throw new RuntimeException("Password must be set");
            SwSessionInfo sessionInfo = new SwSessionInfo();
            sessionInfo.userName = user;
            sessionInfo.userType = authType.name();
            return sessionInfo;
        } catch (IOException e) {
            L.log(Level.SEVERE, "Error logging in", e);
        }
        return null;
    }

    @Override protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        L.info("Void pong response...");
    }
}
