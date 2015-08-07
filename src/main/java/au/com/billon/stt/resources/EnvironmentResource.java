package au.com.billon.stt.resources;

import au.com.billon.stt.db.EnvEntryDAO;
import au.com.billon.stt.db.EnvironmentDAO;
import au.com.billon.stt.models.EnvEntry;
import au.com.billon.stt.models.Environment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 6/30/15.
 */
@Path("/environments") @Produces({ MediaType.APPLICATION_JSON })
public class EnvironmentResource {
    private final EnvironmentDAO dao;
    private final EnvEntryDAO entryDao;

    public EnvironmentResource(EnvironmentDAO dao, EnvEntryDAO entryDao) {
        this.dao = dao;
        this.entryDao = entryDao;
    }

    @POST
    public Environment create(Environment environment) {
        long id = dao.insert(environment);
        environment.setId(id);
        return environment;
    }

    @PUT @Path("{environmentId}")
    public Environment update(Environment environment) {
        dao.update(environment);

        List<EnvEntry> entries = environment.getEntries();
        Map<Long, EnvEntry> entryMap = new HashMap<Long, EnvEntry>();
        long environmentId = environment.getId();

        for (EnvEntry entry: entries) {
            Long entryId = entry.getId();
            if (entryId > 0) {
                // update an existing entry
                entryDao.update(entry);
            } else {
                // insert a new entry
                entry.setEnvironmentId(environmentId);
                entryId = entryDao.insert(entry);
            }
            entryMap.put(entryId, entry);
        }

        // delete existing entries
        List<EnvEntry> dbEntries = entryDao.findByEnv(environmentId);
        for (EnvEntry dbEntry: dbEntries) {
            Long dbEntryId = dbEntry.getId();
            if (entryMap.get(dbEntryId) == null) {
                entryDao.deleteById(dbEntryId);
            }
        }

        return findById(environment.getId());
    }

    @DELETE @Path("{environmentId}")
    public void delete(@PathParam("environmentId") long environmentId) {
        dao.deleteById(environmentId);
    }

    @GET
    public List<Environment> findAll() {
        return dao.findAll();
    }

    @GET @Path("{environmentId}")
    public Environment findById(@PathParam("environmentId") long environmentId) {
        Environment environment = dao.findById(environmentId);
        List<EnvEntry> entries = entryDao.findByEnv(environmentId);
        environment.setEntries(entries);
        return environment;
    }
}
