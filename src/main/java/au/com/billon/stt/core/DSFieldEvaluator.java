package au.com.billon.stt.core;

import au.com.billon.stt.models.DSFieldAssertionProperties;
import au.com.billon.stt.models.TestResponse;
import au.com.billon.stt.models.TestResult;
import au.com.billon.stt.models.Properties;

import java.util.List;
import java.util.Map;

/**
 * Created by adminuser on 8/3/15.
 */
public class DSFieldEvaluator implements Evaluator {
    public static final String CONTAINS_OPERATOR = "Contains";

    public TestResult evaluate(TestResponse response, Properties properties) {
        TestResult result = new TestResult();
        result.setPassed(false);

        DSFieldAssertionProperties assertionProperties = (DSFieldAssertionProperties) properties;

        if (DSFieldEvaluator.CONTAINS_OPERATOR.equals(assertionProperties.getOperator())) {
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) response.getResponseObj();

            for (Map<String, Object> responseMap : responseList) {
                if (assertionProperties.getValue().equals(responseMap.get(assertionProperties.getField()))) {
                    result.setPassed(true);
                    break;
                }
            }
        }

        return result;
    }
}
