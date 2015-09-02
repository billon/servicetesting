package au.com.billon.stt.parsers;

import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Trevor Li on 7/25/15.
 */
public class WSDLParser implements STTParser {
    public String getSampleRequest(Map<String, String> details) {
        Wsdl wsdl = Wsdl.parse(details.get("wsdlUrl"));
        SoapBuilder builder = wsdl.binding().localPart(details.get("wsdlBindingName")).find();
        SoapOperation operation = builder.operation().name(details.get("wsdlOperationName")).find();

        return builder.buildInputMessage(operation);
    }

    public String getAdhocAddress(Map<String, String> details) {
        Wsdl wsdl = Wsdl.parse(details.get("wsdlUrl"));
        SoapBuilder builder = wsdl.binding().localPart(details.get("wsdlBindingName")).find();

        return builder.getServiceUrls().get(0);
    }

    public List<String> getProperties() {
        String[] properties = {"wsdlUrl", "wsdlBindingName", "wsdlOperationName"};
        return Arrays.asList(properties);
    }
}
