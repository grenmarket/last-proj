package integration;

import java.util.Map;

public class ConnectorTest {

    public static void main(String[] args) {
        shouldOrderAndConcatParams();
    }

    static void shouldOrderAndConcatParams() {
        Map<String, String> params = Map.of(
                "custom_param", "6",
                "api_key", "key",
                "callback", "someurl",
                "format", "json"
        );
        System.out.println(Connector.orderAndConcatParams(params).equals("api_keykeycustom_param6"));
    }

}
