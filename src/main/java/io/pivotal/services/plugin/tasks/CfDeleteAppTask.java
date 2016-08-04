package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.tasks.helper.CfDeleteAppTaskDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for Deleting an app
 *
 * @author Biju Kunjummen
 */
public class CfDeleteAppTask extends AbstractCfTask {

	private CfDeleteAppTaskDelegate deleteDelegate = new CfDeleteAppTaskDelegate();

	@TaskAction
	public void deleteApp() {
		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = deleteDelegate.deleteApp(cfOperations, cfAppProperties);
		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	@Override
	public String getDescription() {
		return "Delete an application from Cloud Foundry";
	}
}
