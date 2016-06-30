package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
import io.pivotal.services.plugin.PropertyNameConstants;
import org.gradle.api.DefaultTask;

/**
 * Base class for all Concrete CF tasks
 */
public class AbstractCfTask extends DefaultTask {

	protected CfPushPluginExtension getExtension() {
		return getProject().getExtensions().findByType(CfPushPluginExtension.class);
	}

	protected String getCfApplicationName() {
		if (getProject().hasProperty(PropertyNameConstants.CF_APPLICATION_NAME)) {
			return (String) getProject().property(PropertyNameConstants.CF_APPLICATION_NAME);
		}
		return this.getExtension().getName();
	}

	protected String getAppHostName() {
		if (getProject().hasProperty(PropertyNameConstants.CF_APPLICATION_HOST_NAME)) {
			return (String) getProject().property(PropertyNameConstants.CF_APPLICATION_HOST_NAME);
		}

		return this.getExtension().getHostName();
	}

	protected String getAppDomain() {
		if (getProject().hasProperty(PropertyNameConstants.CF_APPLICATION_DOMAIN)) {
			return (String) getProject().property(PropertyNameConstants.CF_APPLICATION_DOMAIN);
		}

		return this.getExtension().getDomain();

	}
}
