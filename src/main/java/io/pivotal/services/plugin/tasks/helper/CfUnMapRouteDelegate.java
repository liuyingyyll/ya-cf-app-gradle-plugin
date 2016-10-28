package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
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

	public Mono<Void> unmapRoute(CloudFoundryOperations cfOperations, CfProperties cfProperties) {

		LOGGER.lifecycle("Unmapping hostname '{}' in domain '{}' with path '{}' of app '{}'", cfProperties.hostName(),
				cfProperties.domain(), cfProperties.path(), cfProperties.name());

		return cfOperations.routes()
				.unmap(UnmapRouteRequest
						.builder()
						.applicationName(cfProperties.name())
						.domain(cfProperties.domain())
						.host(cfProperties.hostName())
						.path(cfProperties.path())
						.build());


	}

}
