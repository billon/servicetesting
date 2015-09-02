package au.com.billon.stt.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
public class Endpoint {
    private long id;
    private String name;
    private String description;
    private String type;
    private List<EndpointDetail> details;
    private Date created;
    private Date updated;

    public Endpoint() {
    }

    public Endpoint(long id, String name, String description, String type, Date created, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.created = created;
        this.updated = updated;
    }

    public long getId() { return id; }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<EndpointDetail> getDetails() {
        return details;
    }

    public void setDetails(List<EndpointDetail> details) {
        this.details = details;
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
}
