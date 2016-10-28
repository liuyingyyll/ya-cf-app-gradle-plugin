package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfAppStopDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopTask extends AbstractCfTask {

	private CfAppStopDelegate stopDelegate = new CfAppStopDelegate();

	@TaskAction
	public void stopApp() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfProperties cfProperties = getCfProperties();

		Mono<Void> resp = this.stopDelegate.stopApp(cfOperations, cfProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Stop an Application";
	}

}
