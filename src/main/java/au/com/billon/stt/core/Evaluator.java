package au.com.billon.stt.core;

import au.com.billon.stt.models.TestResponse;
import au.com.billon.stt.models.TestResult;
import au.com.billon.stt.models.Properties;

/**
 * Created by Zheng on 27/07/2015.
 */
public interface Evaluator {
    TestResult evaluate(TestResponse response, Properties properties);
}
