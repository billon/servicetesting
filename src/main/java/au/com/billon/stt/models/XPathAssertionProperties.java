package au.com.billon.stt.models;

import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 26/07/2015.
 */
public class XPathAssertionProperties extends Properties {
    private String xpath;
    private String operator;
    private String value;
    private Map<String, String> namespacePrefixes;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getNamespacePrefixes() {
        return namespacePrefixes;
    }

    public void setNamespacePrefixes(Map<String, String> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }
}
