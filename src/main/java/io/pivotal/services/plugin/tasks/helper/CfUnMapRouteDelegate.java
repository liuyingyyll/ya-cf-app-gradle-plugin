package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper responsible for unmapping a route
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfUnMapRouteDelegate.class);

	public Mono<Void> unmapRoute(CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {

		LOGGER.lifecycle("Unmapping hostname '{}' in domain '{}' with path '{}' of app '{}'", cfAppProperties.getHostName(),
				cfAppProperties.getDomain(), cfAppProperties.getPath(), cfAppProperties.getName());

		return cfOperations.routes()
				.unmap(UnmapRouteRequest
						.builder()
						.applicationName(cfAppProperties.getName())
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath())
						.build());


	}

}
