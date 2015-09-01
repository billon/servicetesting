package au.com.billon.stt.models;

/**
 * Created by Zheng on 21/07/2015.
 */
public class SOAPTeststepProperties extends Properties {
    private String wsdlUrl;
    private String wsdlBindingName;
    private String wsdlOperationName;
    private String soapAction;
    private String soapAddress;

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getWsdlBindingName() {
        return wsdlBindingName;
    }

    public void setWsdlBindingName(String wsdlBindingName) {
        this.wsdlBindingName = wsdlBindingName;
    }

    public String getWsdlOperationName() {
        return wsdlOperationName;
    }

    public void setWsdlOperationName(String wsdlOperationName) {
        this.wsdlOperationName = wsdlOperationName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getSoapAddress() {
        return soapAddress;
    }

    public void setSoapAddress(String soapAddress) {
        this.soapAddress = soapAddress;
    }
}
