package au.com.billon.stt.core;

import au.com.billon.stt.models.TestResponse;
import au.com.billon.stt.models.TestResult;
import au.com.billon.stt.models.Properties;
import au.com.billon.stt.models.XPathAssertionProperties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * Created by Zheng on 2/08/2015.
 */
public class XPathEvaluator implements Evaluator {
    public TestResult evaluate(TestResponse response, Properties properties) {
        String responseStr = (String) response.getResponseStr();
        XPathAssertionProperties xPathAssertionProperties = (XPathAssertionProperties) properties;

        TestResult testResult = new TestResult();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new STTNamespaceContext(xPathAssertionProperties.getNamespacePrefixes()));

        String errorMessage = null;
        boolean passed = false;

        try {
            InputSource inputSource = new InputSource(new StringReader(responseStr));
            Object value = xpath.evaluate(xPathAssertionProperties.getXpath(), inputSource, XPathConstants.NODESET);
            NodeList nodeList = (NodeList) value;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getTextContent().equals(xPathAssertionProperties.getValue())) {
                    passed = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        testResult.setError(errorMessage);
        testResult.setPassed(passed);

        return testResult;
    }
}
