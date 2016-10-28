package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfBlueGreenStage2Delegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for a <a href="https://docs.cloudfoundry.org/devguide/deploy-apps/blue-green.html">Blue Green</a> style deployment
 *
 * Will ensure "green" app is made live and "blue" kept in a standby mode..
 *
 * @author Biju Kunjummen
 */
public class CfBlueGreenStage2Task extends AbstractCfTask {

	private CfBlueGreenStage2Delegate blueGreenStage2Delegate = new CfBlueGreenStage2Delegate();

	@TaskAction
	public void runBlueGreen() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfProperties originalProperties = getCfProperties();

		Mono<Void> resp = blueGreenStage2Delegate.runStage2(getProject(), cfOperations, originalProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	@Override
	public String getDescription() {
		return "Push an Application in a Blue-Green no downtime mode - Stage 2";
	}

}
