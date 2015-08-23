package au.com.billon.stt.handlers;

import au.com.billon.stt.models.TestResponse;
import au.com.billon.stt.models.XMLJson;
import org.reficio.ws.client.core.SoapClient;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;

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

        XMLJson responseObj = new XMLJson();
        Map<String, String> namespacePrefixes = new HashMap<String, String>();
        List<Map<String, Object>> jsonGrid = xml2JsonGrid(responseXML, namespacePrefixes);
        responseObj.setJsonGrid(jsonGrid);
        responseObj.setNamespacePrefixes(namespacePrefixes);
        response.setResponseObj(responseObj);

        return response;
    }

    private List<Map<String, Object>> xml2JsonGrid(String responseXML, Map<String, String> namespacePrefixes) throws Exception {
        InputSource inputSource = new InputSource(new StringReader(responseXML));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder loader = factory.newDocumentBuilder();
        Document document = loader.parse(inputSource);

        List<Map<String, Object>> responseObj = element2JsonGrid("", 0, document.getDocumentElement(), namespacePrefixes);

        return responseObj;
    }

    private List<Map<String, Object>> element2JsonGrid(String parentPath, int level, Element element, Map<String, String> namespacePrefixes) {
        List<Map<String, Object>> grid = new ArrayList<Map<String, Object>>();

        String elementName = element.getNodeName();
        String path = parentPath + "/" + elementName;

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("name", elementName);
        row.put("level", level);
        row.put("path", path);
        grid.add(row);

        String prefix = element.getPrefix();
        if (prefix != null) {
            namespacePrefixes.put(prefix, element.getNamespaceURI());
        }

        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i ++) {
            Node node = attributes.item(i);
            if (Node.ATTRIBUTE_NODE == node.getNodeType()) {
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI())) {
                } else {

                }
            }
        }

        NodeList nodes = element.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i ++) {
            Node node = nodes.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element childElement = (Element) node;
                grid.addAll(element2JsonGrid(path, level + 1, childElement, namespacePrefixes));
            } else if (Node.TEXT_NODE == node.getNodeType()) {
                row.put("value", node.getNodeValue());
            }
        }

        return grid;
    }


    public List<String> getPropNames() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
