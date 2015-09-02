package au.com.billon.stt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 9/2/15.
 */
public class TypeMapper {
    private static TypeMapper instance;

    private Map<String, List<String>> intface2endpointTypeMapper = new HashMap<String, List<String>>();

    private TypeMapper() {
        String[][] initialMapper = new String[][] {{"WSDL", "SOAP"}, {"DBInterface", "DB"}};

        for (String[] mapper : initialMapper) {
            List<String> list = new ArrayList<String>();
            for (int i = 1; i< mapper.length; i ++) {
                list.add(mapper[i]);
            }

            intface2endpointTypeMapper.put(mapper[0], list);
        }
    }

    public static synchronized TypeMapper getInstance() {
        if ( instance == null ) {
            instance = new TypeMapper();
        }

        return instance;
    }

    public List<String> getEndpointTypes(String intfaceType) {
        return intface2endpointTypeMapper.get(intfaceType);
    }
}
