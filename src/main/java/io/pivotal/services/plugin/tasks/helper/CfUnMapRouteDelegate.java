package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import reactor.core.publisher.Mono;

/**
 * Helper responsible for unmapping a route
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteDelegate {

	public Mono<Void> unmapRoute(CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {

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
