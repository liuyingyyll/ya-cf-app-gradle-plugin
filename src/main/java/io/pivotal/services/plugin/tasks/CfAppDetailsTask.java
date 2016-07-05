package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.cloudfoundry.operations.applications.GetApplicationManifestRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for exposing the details of an application
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsTask extends AbstractCfTask {

	@TaskAction
	public void deleteApp() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<ApplicationManifest> resp = cfOperations.applications()
				.getApplicationManifest(
						GetApplicationManifestRequest.builder()
								.name(getCfApplicationName())
								.build());


		ApplicationManifest applicationManifest = resp.block(defaultWaitTimeout);

		setApplicationManifest(applicationManifest);
	}

	private void setApplicationManifest(ApplicationManifest applicationManifest) {
		this.getExtension().setApplicationManifest(applicationManifest);
	}

	@Override
	public String getDescription() {
		return "Get the application summary from Cloud Foundry";
	}
}
