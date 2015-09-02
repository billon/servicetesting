package au.com.billon.stt.parsers;

import au.com.billon.stt.models.Properties;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class SPDDBParser implements STTParser {
    public String getSampleRequest(Map<String, String> details) {
        return "select * from ? where ?";
    }

    public String getAdhocAddress(Map<String, String> details) {
        return null;
    }

    public List<String> getProperties() {
        String[] properties = {};
        return Arrays.asList(properties);
    }
}
