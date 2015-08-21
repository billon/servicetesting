package au.com.billon.stt.models;

/**
 * Created by Zheng on 27/07/2015.
 */
public class TestResult {
    //  message of error that occurred during the evaluation
    private String error;

    //  actual value of the expression evaluated against the input
    private String actualValue;

    //  true if assertion verification passed, false otherwise, null if not verified
    private Boolean passed;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }
}
