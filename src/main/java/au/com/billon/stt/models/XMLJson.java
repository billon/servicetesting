package au.com.billon.stt.models;

import au.com.billon.stt.core.STTNamespaceContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 8/23/15.
 */
public class XMLJson {
    private List<Map<String, Object>> jsonGrid;
    private Map<String, String> namespacePrefixes;

    public List<Map<String, Object>> getJsonGrid() {
        return jsonGrid;
    }

    public void setJsonGrid(List<Map<String, Object>> jsonGrid) {
        this.jsonGrid = jsonGrid;
    }

    public Map<String, String> getNamespacePrefixes() {
        return namespacePrefixes;
    }

    public void setNamespacePrefixes(Map<String, String> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }
}
