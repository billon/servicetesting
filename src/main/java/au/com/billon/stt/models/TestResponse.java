package au.com.billon.stt.models;

/**
 * Created by Trevor Li on 8/22/15.
 */
public class TestResponse {
    String responseStr;
    Object responseObj;  // This is mainly used to store the JSON format response

    public String getResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public Object getResponseObj() {
        return responseObj;
    }

    public void setResponseObj(Object responseObj) {
        this.responseObj = responseObj;
    }
}
