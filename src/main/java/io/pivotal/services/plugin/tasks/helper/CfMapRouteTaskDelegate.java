package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import reactor.core.publisher.Mono;

/**
 * Helper class for mapping a route
 *
 * @author Biju Kunjummen
 */
public class CfMapRouteTaskDelegate {

	public Mono<Void> mapRoute(CloudFoundryOperations cfOperations,
							   CfAppProperties cfAppProperties) {

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
