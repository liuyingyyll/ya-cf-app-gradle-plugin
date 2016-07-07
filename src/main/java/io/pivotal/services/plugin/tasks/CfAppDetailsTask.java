package io.pivotal.services.plugin.tasks;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.*;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for exposing the details of an application
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsTask extends AbstractCfTask {

	@TaskAction
	public void appDetails() {

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<ApplicationDetail> resp = cfOperations.applications()
				.get(
						GetApplicationRequest.builder()
								.name(getCfApplicationName())
								.build());


		ApplicationDetail applicationDetail = resp.block(defaultWaitTimeout);

		setApplicationDetail(applicationDetail);
	}

	private void setApplicationDetail(ApplicationDetail applicationDetail) {
		this.getExtension().setApplicationDetail(applicationDetail);
	}

	@Override
	public String getDescription() {
		return "Get the application detail from Cloud Foundry";
	}
}
