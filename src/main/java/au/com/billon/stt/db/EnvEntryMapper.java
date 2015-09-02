package au.com.billon.stt.db;

import au.com.billon.stt.models.Endpoint;
import au.com.billon.stt.models.EnvEntry;
import au.com.billon.stt.models.Environment;
import au.com.billon.stt.models.Intface;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Trevor Li on 7/5/15.
 */
public class EnvEntryMapper implements ResultSetMapper<EnvEntry> {
    public EnvEntry map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        EnvEntry enventry = new EnvEntry(rs.getLong("id"), rs.getLong("environmentId"), rs.getLong("intfaceId"), rs.getLong("endpointId"),
                rs.getTimestamp("created"), rs.getTimestamp("updated"));

        Environment environment = new Environment();
        environment.setName(rs.getString("environmentname"));
        environment.setDescription(rs.getString("environmentdesc"));

        Intface intface = new Intface();
        intface.setName(rs.getString("intfacename"));
        intface.setDescription(rs.getString("intfacedesc"));
        intface.setDeftype("intfaceType");

        Endpoint endpoint = new Endpoint();
        endpoint.setName(rs.getString("endpointname"));
        endpoint.setDescription(rs.getString("endpointdesc"));
        endpoint.setType("endpointType");

        enventry.setEnvironment(environment);
        enventry.setIntface(intface);
        enventry.setEndpoint(endpoint);

        return enventry;
    }
}