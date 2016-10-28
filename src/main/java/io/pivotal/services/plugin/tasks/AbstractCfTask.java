package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPluginExtension;
import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfPropertiesMapper;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Base class for all Concrete CF tasks
 */
abstract class AbstractCfTask extends DefaultTask {

	protected long defaultWaitTimeout = 600_000L; // 10 mins

	static Logger LOGGER = Logging.getLogger(AbstractCfTask.class);

	protected CfPropertiesMapper cfPropertiesMapper;

	protected AbstractCfTask() {
		this.cfPropertiesMapper = new CfPropertiesMapper(getProject());
	}

	protected CloudFoundryOperations getCfOperations() {
		CfProperties cfAppProperties = this.cfPropertiesMapper.getProperties();

		ConnectionContext connectionContext = DefaultConnectionContext.builder()
				.apiHost(cfAppProperties.ccHost())
				.skipSslValidation(true)
				.build();

		TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
				.password(cfAppProperties.ccPassword())
				.username(cfAppProperties.ccUser())
				.build();

		CloudFoundryClient cfClient = ReactorCloudFoundryClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();

		CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(cfClient)
				.organization(cfAppProperties.org())
				.space(cfAppProperties.space())
				.build();

		return cfOperations;
	}

	protected CfPluginExtension getExtension() {
		return this.getProject().getExtensions().findByType(CfPluginExtension.class);
	}

	protected CfProperties getCfProperties() {
		return this.cfPropertiesMapper.getProperties();
	}

	@Override
	public String getGroup() {
		return "Cloud Foundry";
	}
}
