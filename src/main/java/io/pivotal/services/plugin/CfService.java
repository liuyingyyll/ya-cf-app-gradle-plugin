package io.pivotal.services.plugin;

import lombok.Data;

@Data
public class CfService {
    private String name;
    private String plan;
    private String instanceName;
}
