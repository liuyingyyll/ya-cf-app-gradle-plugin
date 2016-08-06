package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.tasks.helper.CfMapRouteDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for handling mappnig route task.
 *
 * @author Biju Kunjummen
 */
public class CfMapRouteTask extends AbstractCfTask {

	private CfMapRouteDelegate mapRouteDelegate = new CfMapRouteDelegate();

	@TaskAction
	public void mapRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = mapRouteDelegate.mapRoute(cfOperations, cfAppProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Add a route for an application";
	}

}
