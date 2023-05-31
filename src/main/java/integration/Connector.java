package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Connector {

    public static final String API_KEY = "1f34704d57ac5a449627672aa2c5c475";
    private static final String API_ROOT = "http://ws.audioscrobbler.com/2.0/";
    private static final String SECRET = "07f24a5362f9e3e78450f84ca44849e7";
    private static final Map<String, String> DEFAULT_PARAMETERS = Map.of(
            "api_key", API_KEY,
            "format", "json"
    );
    private static final Set<String> PROTECTED_PARAMETERS = Set.of("format", "callback");

    public static class Request<T> {
        private final String uri;
        private final Map<String, String> parameters;
        private final Class<T> tClass;

        private Request(String uri, Map<String, String> parameters, Class<T> tClass) {
            this.uri = uri;
            this.parameters = parameters;
            this.tClass = tClass;
        }

        public static <T> Request<T> of(String methodName, Map<String, String> parameters, Class<T> tClass) {
            Map<String, String> params = new HashMap<>();
            params.putAll(DEFAULT_PARAMETERS);
            params.putAll(parameters);
            params.put("method", methodName);
            params = signAndAddParam(params, SECRET);
            Request<T> request = new Request<>(getUri(params), params, tClass);
            System.out.println(request);
            return request;
        }

        private static String getUri(Map<String, String> parameters) {
            return API_ROOT + "?" + parameters.entrySet().stream()
                            .map(e -> e.getKey() + "=" + e.getValue())
                            .collect(Collectors.joining("&"));
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("URI: ").append(uri).append("\n")
                    .append("PARAMS:\n")
                    .append(parameters.entrySet().stream()
                            .map(e -> "\t" + e.getKey() + ": " + e.getValue() + "\n")
                            .collect(Collectors.joining()))
                    .append("TYPE: ").append(tClass.getName())
                    .toString();
        }
    }

    private HttpClient client = createClient();
    private ObjectMapper mapper = new ObjectMapper();

    public <T> T get(Request<T> request) {
        try {
            String response = get(request.uri).body();
            return mapper.readValue(response, request.tClass);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private HttpResponse<String> get(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpClient createClient() {
        return HttpClient.newBuilder()
                .build();
    }

    private static Map<String, String> signAndAddParam(Map<String, String> parameters, String secret) {
        Map<String, String> result = new HashMap<>(parameters);
        result.put("api_sig", signature(parameters, secret));
        return result;
    }
    private static String signature(Map<String, String> parameters, String secret) {
        String concatenated = orderAndConcatParams(parameters);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update((concatenated + secret).getBytes());
        return Hex.encodeHexString(digest.digest());
    }

    static String orderAndConcatParams(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(e -> !PROTECTED_PARAMETERS.contains(e.getKey()))
                .map(e -> e.getKey() + e.getValue())
                .sorted()
                .collect(Collectors.joining());
    }

}
