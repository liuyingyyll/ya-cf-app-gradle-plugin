package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.PropertyNameConstants;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for handling renaming an app.
 *
 * @author Biju Kunjummen
 */
public class CfRenameAppTask extends AbstractCfTask {

	@TaskAction
	public void renameApp() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = cfOperations.applications().rename(RenameApplicationRequest
				.builder()
				.name(getCfApplicationName())
				.newName(getStringPropertyFromProject(
						PropertyNameConstants.CF_APPLICATION_NEW_NAME)
						.orElseThrow(() -> new RuntimeException("New name not provided")))
				.build());

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	@Override
	public String getDescription() {
		return "Rename an Application";
	}
}
