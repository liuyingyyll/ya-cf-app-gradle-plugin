package io.pivotal.services.plugin;

import lombok.Data;

import java.util.Map;

@Data
public class CfUserProvidedService {
    private String instanceName;
    private Map<String, String> credentials;
}
