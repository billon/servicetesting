package au.com.billon.stt.models;

import java.util.List;

/**
 * Created by Zheng on 11/07/2015.
 */
public class WSDLBinding {
    private String name;
    private List<WSDLOperation> operations;

    public WSDLBinding() {}

    public WSDLBinding(String name, List<WSDLOperation> operations) {
        this.name = name;
        this.operations = operations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WSDLOperation> getOperations() {
        return operations;
    }

    public void setOperations(List<WSDLOperation> operations) {
        this.operations = operations;
    }
}
