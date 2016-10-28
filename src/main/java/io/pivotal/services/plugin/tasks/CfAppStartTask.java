package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StartApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for starting an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStartTask extends AbstractCfTask {

	@TaskAction
	public void startApp() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfProperties cfProperties = getCfProperties();

		Mono<Void> resp = cfOperations.applications()
				.start(StartApplicationRequest.builder().name(cfProperties.name()).build());

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	@Override
	public String getDescription() {
		return "Start an Application";
	}

}
