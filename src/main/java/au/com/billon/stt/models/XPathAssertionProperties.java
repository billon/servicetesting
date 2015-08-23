package au.com.billon.stt.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 26/07/2015.
 */
public class XPathAssertionProperties extends Properties {
    private String xPath;
    private String expectedValue;
    private Map<String, String> namespacePrefixes;

    public String getxPath() {
        return xPath;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public Map<String, String> getNamespacePrefixes() {
        return namespacePrefixes;
    }

    public void setNamespacePrefixes(Map<String, String> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }
}
