package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppPluginExtension;
import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.CfAppPropertiesMapper;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.gradle.api.DefaultTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all Concrete CF tasks
 */
abstract class AbstractCfTask extends DefaultTask {

	protected long defaultWaitTimeout = 600_000L; // 10 mins

	static Logger LOGGER = LoggerFactory.getLogger(AbstractCfTask.class);

	protected CfAppPropertiesMapper appPropertiesMapper;

	protected AbstractCfTask() {
		this.appPropertiesMapper = new CfAppPropertiesMapper(getProject());
	}

	protected CloudFoundryOperations getCfOperations() {
		CfAppProperties cfAppProperties = this.appPropertiesMapper.getProperties();

		ConnectionContext connectionContext = DefaultConnectionContext.builder()
				.apiHost(cfAppProperties.getCcHost())
				.skipSslValidation(true)
				.build();

		TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
				.password(cfAppProperties.getCcPassword())
				.username(cfAppProperties.getCcUser())
				.build();

		CloudFoundryClient cfClient = ReactorCloudFoundryClient.builder()
				.connectionContext(connectionContext)
				.tokenProvider(tokenProvider)
				.build();

		CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(cfClient)
				.organization(cfAppProperties.getOrg())
				.space(cfAppProperties.getSpace())
				.build();

		return cfOperations;
	}

	protected CfAppPluginExtension getExtension() {
		return this.getProject().getExtensions().findByType(CfAppPluginExtension.class);
	}

	protected CfAppProperties getCfAppProperties() {
		return this.appPropertiesMapper.getProperties();
	}

	@Override
	public String getGroup() {
		return "Cloud Foundry";
	}
}
