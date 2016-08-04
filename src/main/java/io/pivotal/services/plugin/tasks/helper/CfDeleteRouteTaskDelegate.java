package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.DeleteRouteRequest;
import reactor.core.publisher.Mono;

/**
 * Helper for deleting a route
 *
 * @author Biju Kunjummen
 */
public class CfDeleteRouteTaskDelegate {

	public Mono<Void> deleteRoute(CloudFoundryOperations cfOperations,
								  CfAppProperties cfAppProperties) {

		return cfOperations.routes().delete(
				DeleteRouteRequest
						.builder()
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath())
						.build());

	}

}
