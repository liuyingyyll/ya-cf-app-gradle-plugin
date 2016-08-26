package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfAppStopDelegate.class);

	public Mono<Void> stopApp(CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {

		LOGGER.lifecycle("Stopping app '{}'", cfAppProperties.getName());
		return cfOperations.applications()
				.stop(StopApplicationRequest.builder().name(cfAppProperties.getName()).build());

	}

}
