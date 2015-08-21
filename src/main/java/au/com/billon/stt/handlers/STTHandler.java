package au.com.billon.stt.handlers;

import au.com.billon.stt.models.Endpoint;
import au.com.billon.stt.models.TestResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface STTHandler {
    public TestResponse invoke(String request, Map<String, String> properties) throws Exception;
    public List<String> getPropNames();
}
