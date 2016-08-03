package io.pivotal.services.plugin;

import org.gradle.api.Project;

import java.util.Optional;

public class CfAppPropertiesMapper {

	private final Project project;

	public CfAppPropertiesMapper(Project project) {
		this.project = project;
	}

	private CfAppPluginExtension getExtension() {
		return this.project.getExtensions().findByType(CfAppPluginExtension.class);
	}

	public CfAppProperties getProperties() {
		CfAppProperties cfAppProperties = CfAppProperties.builder()
				.name(getCfApplicationName())
				.buildpack(getBuildpack())
				.ccHost(getCcHost())
				.ccPassword(getCcPassword())
				.ccUser(getCcUser())
				.ccPassword(getCcPassword())
				.org(getOrg())
				.space(getSpace())
				.domain(getAppDomain())
				.path(getCfPath())
				.command(getCommand())
				.console(this.getExtension().getConsole())
				.debug(this.getExtension().getDebug())
				.detectedStartCommand(this.getExtension().getDetectedStartCommand())
				.diskQuota(getDiskQuota())
				.enableSsh(this.getExtension().getEnableSsh())
				.environment(this.getExtension().getEnvironment())
				.filePath(getFilePath())
				.healthCheckType(this.getExtension().getHealthCheckType())
				.hostName(getAppHostName())
				.instances(getInstances())
				.memory(getMemory())
				.ports(this.getExtension().getPorts())
				.state(this.getExtension().getState())
				.timeout(getTimeout())
				.build();
		return cfAppProperties;
	}

	public CfAppProperties copyPropertiesWithNameChange(CfAppProperties original, String newName) {
		CfAppProperties cfAppProperties = CfAppProperties.builder()
				.name(newName)
				.buildpack(original.getBuildpack())
				.ccHost(original.getCcHost())
				.ccPassword(original.getCcPassword())
				.ccUser(original.getCcUser())
				.ccPassword(original.getCcPassword())
				.org(original.getOrg())
				.space(original.getSpace())
				.domain(original.getDomain())
				.path(original.getPath())
				.command(original.getCommand())
				.console(original.getConsole())
				.debug(original.getDebug())
				.detectedStartCommand(original.getDetectedStartCommand())
				.diskQuota(original.getDiskQuota())
				.enableSsh(original.getEnableSsh())
				.environment(original.getEnvironment())
				.filePath(original.getFilePath())
				.healthCheckType(original.getHealthCheckType())
				.hostName(original.getHostName())
				.instances(original.getInstances())
				.memory(original.getMemory())
				.ports(original.getPorts())
				.state(original.getState())
				.timeout(original.getTimeout())
				.build();
		return cfAppProperties;
	}

	public String getCfApplicationName() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NAME)
				.orElse(this.getExtension().getName());
	}

	public String getNewName() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NEW_NAME)
				.orElse(null);
	}

	public String getCommand() {
		return this.getExtension().getCommand();
	}

	public String getAppHostName() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_HOST_NAME)
				.orElse(this.getExtension().getHostName());
	}

	public String getAppDomain() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_DOMAIN)
				.orElse(this.getExtension().getDomain());
	}

	public String getFilePath() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_FILE_PATH)
				.orElse(this.getExtension().getFilePath());
	}

	public String getCcHost() {
		return getStringPropertyFromProject(PropertyNameConstants.CC_HOST)
				.orElse(this.getExtension().getCcHost());
	}

	public String getCcUser() {
		return getStringPropertyFromProject(PropertyNameConstants.CC_USER)
				.orElse(this.getExtension().getCcUser());
	}

	public String getCcPassword() {
		return getStringPropertyFromProject(PropertyNameConstants.CC_PASSWORD)
				.orElse(this.getExtension().getCcPassword());
	}

	public String getBuildpack() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_BUILDPACK)
				.orElse(this.getExtension().getBuildpack());
	}

	public String getOrg() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_ORG)
				.orElse(this.getExtension().getOrg());
	}

	public String getSpace() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_SPACE)
				.orElse(this.getExtension().getSpace());
	}

	public String getCfPath() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_PATH)
				.orElse(this.getExtension().getPath());
	}

	public Integer getInstances() {
		return getIntegerProperty(PropertyNameConstants.CF_INSTANCES)
				.orElse(this.getExtension().getInstances());
	}

	public Integer getMemory() {
		return getIntegerProperty(PropertyNameConstants.CF_MEMORY)
				.orElse(this.getExtension().getMemory());
	}

	public Integer getTimeout() {
		return getIntegerProperty(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT)
				.orElse(this.getExtension().getTimeout());
	}

	public Integer getDiskQuota() {
		return getIntegerProperty(PropertyNameConstants.CF_DISK_QUOTA)
				.orElse(this.getExtension().getDiskQuota());

	}

	public Integer getStagingTimeout() {
		return 15;
	}

	public Integer getStartupTimeout() {
		return 5;
	}

	public Optional<String> getStringPropertyFromProject(String propertyName) {
		if (this.project.hasProperty(propertyName)) {
			return Optional.of((String) this.project.property(propertyName));
		}
		return Optional.empty();
	}


	public Optional<Integer> getIntegerProperty(String propertyName) {
		if (this.project.hasProperty(propertyName)) {
			return Optional.of((Integer) this.project.property(propertyName));
		}
		return Optional.empty();
	}
}
