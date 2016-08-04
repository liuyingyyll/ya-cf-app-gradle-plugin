package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.tasks.helper.CfBlueGreenStage1Delegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for a <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/blue-green.html">Blue Green</a> style deployment
 *
 * Upto the point of getting a new application with a new route deployed to CF. There will be subsequent stage which
 * will make this new app the primary app and remove the old app routes
 *
 * @author Biju Kunjummen
 */
public class CfBlueGreenStage1Task extends AbstractCfTask {

	private CfBlueGreenStage1Delegate blueGreenStage1Delegate = new CfBlueGreenStage1Delegate();

	@TaskAction
	public void runBlueGreen() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties originalProperties = getCfAppProperties();

		Mono<Void> resp = blueGreenStage1Delegate.runStage1(getProject(), cfOperations, originalProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	@Override
	public String getDescription() {
		return "Push an Application in a Blue-Green no downtime mode - Stage 1";
	}

}
