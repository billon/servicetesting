package au.com.billon.stt.models;

import au.com.billon.stt.utils.STTUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by Zheng on 19/07/2015.
 */
public class AssertionPropertiesDeserializer extends JsonDeserializer<Properties> {

    @Override
    public Properties deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Assertion assertion = (Assertion) jsonParser.getCurrentValue();
        Properties properties = (Properties) new ObjectMapper().treeToValue(
                node, STTUtils.getAssertionPropertiesClassByType(assertion.getType()));
        return properties;
    }
}
