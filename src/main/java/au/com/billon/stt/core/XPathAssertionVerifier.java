package au.com.billon.stt.core;

import au.com.billon.stt.models.*;

/**
 * Created by Zheng on 6/08/2015.
 */
public class XPathAssertionVerifier implements AssertionVerifier {
    public XPathAssertionVerifier() {}

    public Assertion verify(Assertion assertion) {
        AssertionVerification verification = assertion.getVerification();
        XPathAssertionProperties assertionProperties = (XPathAssertionProperties) assertion.getProperties();
        EvaluationResponse evaluationResponse = new EvaluatorFactory().createEvaluator("WSDL", assertion.getType()).evaluate(verification.getInput(), assertionProperties);
        verification.setPassed(evaluationResponse.getError() == null &&
                assertionProperties.getExpectedValue().equals(evaluationResponse.getActualValue()));
        verification.setError(evaluationResponse.getError());
        verification.setActualValue(evaluationResponse.getActualValue());
        return assertion;
    }
}
