package au.com.billon.stt.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Zheng on 7/07/2015.
 */
public class Teststep {
    public static final String TEST_STEP_TYPE_SOAP = "SOAP";
    private long id;
    private long testcaseId;
    private String name;
    private String type;
    private String description;
    private long sequence;
    private long intfaceId;
    private Intface intface;
    private Map<String, String> properties;
    private String request;
    private long endpointId;
    private Endpoint endpoint;
    private List<Assertion> assertions;
    private TestResult result;
    private Date created;
    private Date updated;

    public Teststep() {}

    public Teststep(long id, long testcaseId, String name, String type, String description, long sequence,
                    Map<String, String> properties, Date created, Date updated, String request, long intfaceId, long endpointId) {
        this.id = id;
        this.testcaseId = testcaseId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.sequence = sequence;
        this.properties = properties;
        this.created = created;
        this.updated = updated;
        this.request = request;
        this.intfaceId = intfaceId;
        this.endpointId = endpointId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public long getIntfaceId() {
        return intfaceId;
    }

    public void setIntfaceId(long intfaceId) {
        this.intfaceId = intfaceId;
    }

    public long getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(long endpointId) {
        this.endpointId = endpointId;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Intface getIntface() {
        return intface;
    }

    public void setIntface(Intface intface) { this.intface = intface; }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
