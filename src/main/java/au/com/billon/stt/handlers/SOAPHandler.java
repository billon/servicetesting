package au.com.billon.stt.handlers;

import au.com.billon.stt.models.TestResponse;
import au.com.billon.stt.utils.XMLUtils;
import org.reficio.ws.client.core.SoapClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPHandler implements STTHandler {
    public SOAPHandler() { }

    public TestResponse invoke(String request, Map<String, String> properties) throws Exception {
        TestResponse response = new TestResponse();

        SoapClient client = SoapClient.builder().endpointUri(properties.get("url")).build();
        String responseXML = client.post(request);
        response.setResponseStr(responseXML);

        return response;
    }

    public List<String> getPropNames() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
