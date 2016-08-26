package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.DeleteRouteRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper for deleting a route
 *
 * @author Biju Kunjummen
 */
public class CfDeleteRouteDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfDeleteRouteDelegate.class);

	public Mono<Void> deleteRoute(CloudFoundryOperations cfOperations,
								  CfAppProperties cfAppProperties) {

		LOGGER.lifecycle("Deleting hostname '{}' in domain '{}' with path '{}' of app '{}'", cfAppProperties.getHostName(),
				cfAppProperties.getDomain(), cfAppProperties.getPath(), cfAppProperties.getName());

		return cfOperations.routes().delete(
				DeleteRouteRequest
						.builder()
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath())
						.build());

	}

}
