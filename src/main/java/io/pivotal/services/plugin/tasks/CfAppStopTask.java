package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopTask extends AbstractCfTask {

	@TaskAction
	public void stopApp() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.applications()
				.stop(StopApplicationRequest.builder().name(getCfApplicationName()).build());

		resp.block(defaultWaitTimeout);

	}

	@Override
	public String getDescription() {
		return "Stop an Application";
	}

}
