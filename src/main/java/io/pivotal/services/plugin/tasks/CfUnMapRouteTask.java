package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for handling unmap route task.
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteTask extends AbstractCfTask {

	@TaskAction
	public void unmapRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = cfOperations.routes()
				.unmap(UnmapRouteRequest
						.builder()
						.applicationName(cfAppProperties.getName())
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath())
						.build());

		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Remove an existing route for an application";
	}

}
