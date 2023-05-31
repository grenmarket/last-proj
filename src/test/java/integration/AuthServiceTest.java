package integration;

import integration.dto.Session;

import java.io.IOException;

public class AuthServiceTest {

    public static void main(String[] args) {
        String token = shouldFetchAuthToken();
        shouldOpenBrowser(token);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        shouldGetSession(token);
    }

    static String shouldFetchAuthToken() {
        AuthService authService = new AuthService();
        String token = authService.fetchRequestToken().token();
        System.out.println(token.length() == 32);
        return token;
    }

    static void shouldOpenBrowser(String token) {
        AuthService authService = new AuthService();
        try {
            authService.openBrowser(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void shouldGetSession(String token) {
        AuthService authService = new AuthService();
        Session session = authService.fetchSessionKey(token);
        System.out.println(session.getSession().getKey());
    }

}
