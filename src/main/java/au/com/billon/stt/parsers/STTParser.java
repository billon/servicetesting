package au.com.billon.stt.parsers;

import au.com.billon.stt.models.Properties;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public interface STTParser {
    public String getSampleRequest(Map<String, String> details);
    public String getAdhocAddress(Map<String, String> details);
    public List<String> getProperties();
}
