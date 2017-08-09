package io.pivotal.services.plugin;

import java.util.Map;

public class CfUserProvidedService {
    private String instanceName;
    private Map<String, String> credentials;

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }
}
