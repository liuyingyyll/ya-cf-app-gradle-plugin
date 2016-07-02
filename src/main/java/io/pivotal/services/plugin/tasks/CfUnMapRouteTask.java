package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for handling unmap route task.
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteTask extends AbstractCfTask {

	@TaskAction
	public void unmapRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.routes()
				.unmap(UnmapRouteRequest
						.builder()
						.applicationName(getCfApplicationName())
						.domain(getAppDomain())
						.host(getAppHostName()).build());

		resp.block(600_000L);

	}

	@Override
	public String getDescription() {
		return "Remove an existing route for an application";
	}

}
