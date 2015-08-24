package au.com.billon.stt.handlers;

import au.com.billon.stt.models.TestResponse;
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
    private static final int Return_Row_Limit = 100;

    public DBHandler() { }

    public TestResponse invoke(String request, Map<String, String> properties) throws Exception {
        TestResponse response = new TestResponse();

        String dbUrl = properties.get("url");

        String rowLimitRequest = request;
        if (dbUrl.toLowerCase().startsWith("jdbc:h2")) {
            rowLimitRequest = "select * from ( " + request + " ) where rownum() <= " + Return_Row_Limit;
        } else if (dbUrl.toLowerCase().startsWith("jdbc:oracle")) {
            rowLimitRequest = "select * from ( " + request + " ) where rownum <= " + Return_Row_Limit;
        }

        DBI jdbi = new DBI(dbUrl, properties.get("username"), properties.get("password"));
        Handle handle = jdbi.open();

        Query<Map<String, Object>> query = handle.createQuery(rowLimitRequest);
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

        response.setResponseObj(results);

        return response;
    }

    public List<String> getPropNames() {
        String[] properties = {"url", "username", "password"};
        return Arrays.asList(properties);
    }
}
