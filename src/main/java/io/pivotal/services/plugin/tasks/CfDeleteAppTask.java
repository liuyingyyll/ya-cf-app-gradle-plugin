package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for Deleting an app
 *
 * @author Biju Kunjummen
 */
public class CfDeleteAppTask extends AbstractCfTask {

	@TaskAction
	public void deleteApp() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.applications().delete(
				DeleteApplicationRequest
						.builder()
						.name(getCfApplicationName())
						.build());

		resp.block(600_000L);
	}

	@Override
	public String getDescription() {
		return "Delete an application from Cloud Foundry";
	}
}
