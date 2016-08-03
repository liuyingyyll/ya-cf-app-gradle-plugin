package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopTask extends AbstractCfTask {

	@TaskAction
	public void stopApp() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = cfOperations.applications()
				.stop(StopApplicationRequest.builder().name(cfAppProperties.getName()).build());

		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Stop an Application";
	}

}
