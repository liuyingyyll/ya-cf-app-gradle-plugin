package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper class for mapping a route
 *
 * @author Biju Kunjummen
 */
public class CfMapRouteDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfMapRouteDelegate.class);

	public Mono<Void> mapRoute(CloudFoundryOperations cfOperations,
							   CfAppProperties cfAppProperties) {

		LOGGER.lifecycle("Mapping hostname '{}' in domain '{}' with path '{}' of app '{}'", cfAppProperties.getHostName(),
				cfAppProperties.getDomain(), cfAppProperties.getPath(), cfAppProperties.getName());

		Mono<Void> resp = cfOperations.routes()
				.map(MapRouteRequest
						.builder()
						.applicationName(cfAppProperties.getName())
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath()).build());

		return resp;

	}

}
