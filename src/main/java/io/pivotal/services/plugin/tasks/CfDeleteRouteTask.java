package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.DeleteRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for Deleting a route
 *
 * @author Biju Kunjummen
 */
public class CfDeleteRouteTask extends AbstractCfTask {

	@TaskAction
	public void deleteRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.routes().delete(
				DeleteRouteRequest
						.builder()
						.domain(getAppDomain())
						.host(getAppHostName())
						.path(getCfPath())
						.build());

		resp.block(defaultWaitTimeout);
	}


	@Override
	public String getDescription() {
		return "Delete a route from Cloud Foundry";
	}
}
