package au.com.billon.stt.core;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 1/08/2015.
 */
public class STTNamespaceContext implements NamespaceContext {
    private Map<String, String> namespacePrefixes;

    public STTNamespaceContext(Map<String, String> namespacePrefixes) {
        this.namespacePrefixes = namespacePrefixes;
    }

    public String getNamespaceURI(String prefix) {
        return namespacePrefixes.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
