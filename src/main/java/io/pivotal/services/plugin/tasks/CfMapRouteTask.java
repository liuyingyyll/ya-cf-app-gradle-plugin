package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for handling mappnig route task.
 *
 * @author Biju Kunjummen
 */
public class CfMapRouteTask extends AbstractCfTask {

	@TaskAction
	public void mapRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = cfOperations.routes()
				.map(MapRouteRequest
						.builder()
						.applicationName(cfAppProperties.getName())
						.domain(cfAppProperties.getDomain())
						.host(cfAppProperties.getHostName())
						.path(cfAppProperties.getPath()).build());

		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Add a route for an application";
	}

}
