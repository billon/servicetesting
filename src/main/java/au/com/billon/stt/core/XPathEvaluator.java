package au.com.billon.stt.core;

import au.com.billon.stt.models.TestResult;
import au.com.billon.stt.models.Properties;
import au.com.billon.stt.models.XPathAssertionProperties;
import au.com.billon.stt.utils.XMLUtils;
import com.sun.org.apache.xpath.internal.XPathException;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * Created by Zheng on 2/08/2015.
 */
public class XPathEvaluator implements Evaluator {
    public TestResult evaluate(Object response, Properties properties) {
        String responseStr = (String) response;
        XPathAssertionProperties xPathAssertionProperties = (XPathAssertionProperties) properties;

        TestResult testResult = new TestResult();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new STTNamespaceContext(xPathAssertionProperties.getNamespacePrefixes()));

        String actualValue = null;
        String errorMessage = null;
        try {
            InputSource inputSource = new InputSource(new StringReader(responseStr));
            Object value = xpath.evaluate(xPathAssertionProperties.getxPath(), inputSource, XPathConstants.NODESET);
            actualValue = XMLUtils.domNodeListToString((NodeList) value);
        } catch (XPathExpressionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof XPathException &&
                    cause.getMessage().startsWith("Can not convert") && cause.getMessage().endsWith("!")) {
                //  The value is not of type NODESET. Swallow the exception and try STRING.
                InputSource inputSource2 = new InputSource(new StringReader(responseStr));
                try {
                    actualValue = (String) xpath.evaluate(xPathAssertionProperties.getxPath(), inputSource2, XPathConstants.STRING);
                } catch (XPathExpressionException e1) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }
            } else {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        testResult.setError(errorMessage);
        testResult.setActualValue(actualValue);

        return testResult;
    }
}
