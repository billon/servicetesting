package au.com.billon.stt.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 1/07/2015.
 */
public class Testcase {
    private long id;
    private String name;
    private String description;
    private List<Teststep> teststeps;
    private Long environmentId;
    private Environment environment;
    private TestResult result;
    private Date created;
    private Date updated;

    public Testcase() {}

    public Testcase(long id, String name, String description, Long environmentId, Date created, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.environmentId = environmentId;
        this.created = created;
        this.updated = updated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        if (environmentId == 0) {
            this.environmentId = null;
        } else {
            this.environmentId = environmentId;
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
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

    public List<Teststep> getTeststeps() {
        return teststeps;
    }

    public void setTeststeps(List<Teststep> teststeps) {
        this.teststeps = teststeps;
    }
}
