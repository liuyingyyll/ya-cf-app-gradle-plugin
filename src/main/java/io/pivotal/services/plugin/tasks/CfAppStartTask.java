package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for starting an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStartTask extends AbstractCfTask {

	@TaskAction
	public void startApp() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.applications()
				.start(StartApplicationRequest.builder().name(getCfApplicationName()).build());

		resp.block(defaultWaitTimeout);

	}

	@Override
	public String getDescription() {
		return "Start an Application";
	}

}
