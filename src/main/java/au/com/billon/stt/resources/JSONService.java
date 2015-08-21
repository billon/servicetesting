package au.com.billon.stt.resources;

import au.com.billon.stt.core.EvaluatorFactory;
import au.com.billon.stt.models.Assertion;
import au.com.billon.stt.models.AssertionVerification;
import au.com.billon.stt.models.TestResult;
import au.com.billon.stt.models.XPathAssertionProperties;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A pseudo JSON RPC service for hosting action oriented browser-server interactions.
 * It is not based on JSON-RPC spec, nor is it a REST resource.
 * Created by Zheng on 27/07/2015.
 */
@Path("/jsonservice") @Produces({ MediaType.APPLICATION_JSON })
public class JSONService {
    @POST @Path("verifyassertion")
    public Assertion verifyAssertion(Assertion assertion) {
        AssertionVerification verification = assertion.getVerification();
        XPathAssertionProperties assertionProperties = (XPathAssertionProperties) assertion.getProperties();
        TestResult testResult = new EvaluatorFactory().createEvaluator("WSDL", assertion.getType()).evaluate(verification.getInput(), assertionProperties);
        verification.setPassed(testResult.getError() == null &&
                assertionProperties.getExpectedValue().equals(testResult.getActualValue()));
        verification.setError(testResult.getError());
        verification.setActualValue(testResult.getActualValue());
        return assertion;
    }
}
