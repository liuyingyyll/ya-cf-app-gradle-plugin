package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppPluginExtension;
import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RestageApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for cf restage.
 *
 * @author Biju Kunjummen
 */
public class CfRestageTask extends AbstractCfTask {

	@TaskAction
	public void restage() {
		CfAppProperties cfAppProperties = getCfAppProperties();
		LOGGER.info("About to call Restage task : {} ", cfAppProperties.toString());

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.applications().restage(RestageApplicationRequest.builder()
				.name(cfAppProperties.getName())
				.stagingTimeout(Duration.ofMinutes(cfAppProperties.getStagingTimeout()))
				.startupTimeout(Duration.ofMinutes(cfAppProperties.getStartupTimeout())).build()
		);

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	@Override
	public String getDescription() {
		return "Restage an Application";
	}
}
