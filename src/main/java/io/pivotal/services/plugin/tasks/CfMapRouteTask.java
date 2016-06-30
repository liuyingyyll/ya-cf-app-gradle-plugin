package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for handling mappnig route task.
 *
 * @author Biju Kunjummen
 */
public class CfMapRouteTask extends AbstractCfTask {

	@TaskAction
	public void mapRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.routes()
				.map(MapRouteRequest
						.builder()
						.applicationName(getCfApplicationName())
						.domain(getAppDomain())
						.host(getAppHostName()).build());

		resp.block(600_000L);

	}

}
