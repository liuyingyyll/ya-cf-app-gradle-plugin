package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.DeleteRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for Deleting a route
 *
 * @author Biju Kunjummen
 */
public class CfDeleteRouteTask extends AbstractCfTask {

	@TaskAction
	public void deleteRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = cfOperations.routes().delete(
				DeleteRouteRequest
						.builder()
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath())
						.build());

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}


	@Override
	public String getDescription() {
		return "Delete a route from Cloud Foundry";
	}
}
