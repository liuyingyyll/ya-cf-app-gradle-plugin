package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfUnMapRouteDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for handling unmap route task.
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteTask extends AbstractCfTask {

	private CfUnMapRouteDelegate unMapRouteTaskDelegate = new CfUnMapRouteDelegate();

	@TaskAction
	public void unmapRoute() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfProperties cfProperties = getCfProperties();

		Mono<Void> resp = this.unMapRouteTaskDelegate.unmapRoute(cfOperations, cfProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Remove an existing route for an application";
	}

}
