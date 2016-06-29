package io.pivotal.services.plugin;

import org.cloudfoundry.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Holds configuration related to cf-push
 */
public class CfPushPluginExtension {
    private String name;
    private String path;
    private String host;
    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String org;
    private String space;
    private String state;
    private String buildpack;
    private String command;
    private Boolean console;
    private Boolean debug;
    private String detectedStartCommand;
    private Integer diskQuota;
    private Boolean enableSsh;
    private Map<String, String> environment;
    private Integer healthCheckTimeout;
    private String healthCheckType;
    private Integer instances;
    private Integer memory;
    private List<Integer> ports;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBuildpack() {
        return buildpack;
    }

    public void setBuildpack(String buildpack) {
        this.buildpack = buildpack;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getConsole() {
        return console;
    }

    public void setConsole(Boolean console) {
        this.console = console;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getDetectedStartCommand() {
        return detectedStartCommand;
    }

    public void setDetectedStartCommand(String detectedStartCommand) {
        this.detectedStartCommand = detectedStartCommand;
    }

    public Integer getDiskQuota() {
        return diskQuota;
    }

    public void setDiskQuota(Integer diskQuota) {
        this.diskQuota = diskQuota;
    }

    public Boolean getEnableSsh() {
        return enableSsh;
    }

    public void setEnableSsh(Boolean enableSsh) {
        this.enableSsh = enableSsh;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public Integer getHealthCheckTimeout() {
        return healthCheckTimeout;
    }

    public void setHealthCheckTimeout(Integer healthCheckTimeout) {
        this.healthCheckTimeout = healthCheckTimeout;
    }

    public String getHealthCheckType() {
        return healthCheckType;
    }

    public void setHealthCheckType(String healthCheckType) {
        this.healthCheckType = healthCheckType;
    }

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
