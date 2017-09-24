package io.pivotal.services.plugin;

import java.util.Map;

public class CfUserProvidedService {
    private String instanceName;
    private Map<String, String> credentials;

    private Integer completionTimeout;

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

    public Integer getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(Integer completionTimeout) {
        this.completionTimeout = completionTimeout;
    }
}
