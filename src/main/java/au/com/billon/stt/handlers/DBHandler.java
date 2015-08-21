package au.com.billon.stt.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class DBHandler implements STTHandler {
    public DBHandler() { }

    public Object invoke(String request, Map<String, String> details) throws Exception {
        DBI jdbi = new DBI(details.get("url"), details.get("username"), details.get("password"));
        Handle handle = jdbi.open();

        Query<Map<String, Object>> query = handle.createQuery(request);
        List<Map<String, Object>> results = query.list();

        // ObjectMapper mapper = new ObjectMapper();
        // mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // StringWriter responseWriter = new StringWriter();
        // mapper.writeValue(responseWriter, results);

        handle.close();

        // convert the result values to String
        for (Map<String, Object> result : results) {
            Set<String> keys = result.keySet();
            for (String key : keys) {
                result.put(key, result.get(key).toString());
            }
        }

        return results;
    }

    public List<String> getProperties() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
