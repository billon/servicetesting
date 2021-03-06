package au.com.billon.stt.models;

import java.util.Date;

/**
 * Created by Trevor Li on 7/4/15.
 */
public class Intface {
    private long id;
    private String name;
    private String description;
    private String deftype;
    private String defurl;
    private Date created;
    private Date updated;

    public Intface() {
    }

    public Intface(long id, String name, String description, String deftype, String defurl, Date created, Date updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deftype = deftype;
        this.defurl = defurl;
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

    public String getDeftype() {
        return deftype;
    }

    public void setDeftype(String deftype) {
        this.deftype = deftype;
    }

    public String getDefurl() {
        return defurl;
    }

    public void setDefurl(String defurl) {
        this.defurl = defurl;
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
