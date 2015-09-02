package au.com.billon.stt.db;

import au.com.billon.stt.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Zheng on 11/07/2015.
 */
public class TeststepMapper implements ResultSetMapper<Teststep> {
    public Teststep map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        String type = rs.getString("type");
        Map<String, String> properties = null;
        try {
            properties = (new ObjectMapper()).readValue(rs.getString("properties"), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Teststep teststep = new Teststep(rs.getLong("id"), rs.getLong("testcase_id"), rs.getString("name"),
                rs.getString("type"), rs.getString("description"), rs.getLong("sequence"), properties, rs.getTimestamp("created"),
                rs.getTimestamp("updated"), rs.getString("request"), rs.getLong("intfaceId"), rs.getLong("endpointId"));

        Intface intface = new Intface();
        intface.setId(rs.getLong("intfaceId"));
        intface.setName(rs.getString("intfaceName"));

        teststep.setIntface(intface);

        Endpoint endpoint = new Endpoint();
        endpoint.setId(rs.getLong("endpointId"));
        endpoint.setName(rs.getString("endpointName"));

        teststep.setEndpoint(endpoint);

        return teststep;
    }
}
