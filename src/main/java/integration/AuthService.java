package integration;

import integration.dto.AuthToken;
import integration.dto.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static integration.Connector.API_KEY;

public class AuthService {
    private static final String AUTH_GET_TOKEN = "auth.gettoken";
    private static final String AUTH_GET_SESSION = "auth.getsession";

    private Connector connector = new Connector();

    AuthToken fetchRequestToken() {
        return connector.get(Connector.Request.of(AUTH_GET_TOKEN, new HashMap<>(), AuthToken.class));
    }

    void openBrowser(String token) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String authUrl = "http://last.fm/api/auth/?api_key=" + API_KEY + "&token=" + token;
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) {
            runtime.exec("rundll32 url.dll,FileProtocolHandler " + authUrl);
        } else if (os.contains("MAC")) {
            runtime.exec("open " + authUrl);
        } else if (os.contains("NIX") || os.contains("NUX")) {
            runtime.exec("xdg-open " + authUrl);
        }
    }

    Session fetchSessionKey(String token) {
        Map<String, String> params = Map.of("token", token);
        return connector.get(Connector.Request.of(AUTH_GET_SESSION, params, Session.class));
    }

}
