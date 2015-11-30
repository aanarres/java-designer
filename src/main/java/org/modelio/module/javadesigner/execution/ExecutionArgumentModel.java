package org.modelio.module.javadesigner.execution;


public class ExecutionArgumentModel {
    private String arguments = "";
    
    private String vmArguments = "";

    public ExecutionArgumentModel(String arguments,
            String vmArguments) {
        this.arguments = arguments;
        this.vmArguments = vmArguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setVmArguments(String vmArguments) {
        this.vmArguments = vmArguments;
    }

    public String getVmArguments() {
        return this.vmArguments;
    }
}
