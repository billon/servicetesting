package au.com.billon.stt.handlers;

import au.com.billon.stt.models.TestResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class MQHandler implements STTHandler {
    public MQHandler() { }

    public TestResponse invoke(String request, Map<String, String> properties) throws Exception {
        return null;
    }

    public List<String> getPropNames() {
        String[] properties = {"host", "port", "channel", "manager", "queue", "userid", "password"};
        return Arrays.asList(properties);
    }
}
