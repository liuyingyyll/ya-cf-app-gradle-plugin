package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
import io.pivotal.services.plugin.PropertyNameConstants;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.gradle.api.DefaultTask;

import java.util.Optional;

/**
 * Base class for all Concrete CF tasks
 */
abstract class AbstractCfTask extends DefaultTask {

	protected long defaultWaitTimeout = 600_000L; // 10 mins

	protected CloudFoundryOperations getCfOperations() {
		CloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
				.host(getCcHost())
				.username(getCcUser())
				.password(getCcPassword())
				.skipSslValidation(true)
				.build();


		CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(cfClient)
				.organization(getOrg())
				.space(getSpace())
				.build();

		return cfOperations;
	}

	protected CfPushPluginExtension getExtension() {
		return getProject().getExtensions().findByType(CfPushPluginExtension.class);
	}

	protected String getCfApplicationName() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NAME)
				.orElse(this.getExtension().getName());
	}

	protected String getAppHostName() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_HOST_NAME)
				.orElse(this.getExtension().getHostName());
	}

	protected String getAppDomain() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_DOMAIN)
				.orElse(this.getExtension().getDomain());
	}

	protected String getFilePath() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_FILE_PATH)
				.orElse(this.getExtension().getFilePath());
	}

	protected String getCcHost() {
		return getStringPropertyFromProject(PropertyNameConstants.CC_HOST)
				.orElse(this.getExtension().getCcHost());
	}

	protected String getCcUser() {
		return getStringPropertyFromProject(PropertyNameConstants.CC_USER)
				.orElse(this.getExtension().getCcUser());
	}

	protected String getCcPassword() {
		return getStringPropertyFromProject(PropertyNameConstants.CC_PASSWORD)
				.orElse(this.getExtension().getCcPassword());
	}

	protected String getBuildpack() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_BUILDPACK)
				.orElse(this.getExtension().getBuildpack());
	}

	protected String getOrg() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_ORG)
				.orElse(this.getExtension().getOrg());
	}

	protected String getSpace() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_SPACE)
				.orElse(this.getExtension().getSpace());
	}

	protected String getCfPath() {
		return getStringPropertyFromProject(PropertyNameConstants.CF_PATH)
				.orElse(this.getExtension().getPath());
	}

	protected Integer getInstances() {
		return getIntegerProperty(PropertyNameConstants.CF_INSTANCES)
				.orElse(this.getExtension().getInstances());
	}

	protected Integer getMemory() {
		return getIntegerProperty(PropertyNameConstants.CF_MEMORY)
				.orElse(this.getExtension().getMemory());
	}

	protected Integer getHealthCheckTimeout() {
		return getIntegerProperty(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT)
				.orElse(this.getExtension().getHealthCheckTimeout());
	}

	protected Integer getDiskQuota() {
		return getIntegerProperty(PropertyNameConstants.CF_DISK_QUOTA)
				.orElse(this.getExtension().getDiskQuota());

	}

	protected Optional<String> getStringPropertyFromProject(String propertyName) {
		if (getProject().hasProperty(propertyName)) {
			return Optional.of((String) getProject().property(propertyName));
		}
		return Optional.empty();
	}


	protected Optional<Integer> getIntegerProperty(String propertyName) {
		if (getProject().hasProperty(propertyName)) {
			return Optional.of((Integer) getProject().property(propertyName));
		}
		return Optional.empty();
	}


	@Override
	public String getGroup() {
		return "Cloud Foundry";
	}
}
