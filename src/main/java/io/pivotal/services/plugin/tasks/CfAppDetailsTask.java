package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.helper.CfAppDetailsTaskDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * Responsible for exposing the details of an application
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsTask extends AbstractCfTask {

	private CfAppDetailsTaskDelegate detailsTaskDelegate = new CfAppDetailsTaskDelegate();

	@TaskAction
	public void appDetails() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Optional<ApplicationDetail>> resp = detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties);

		Optional<ApplicationDetail> applicationDetail = resp.block(Duration.ofMillis(defaultWaitTimeout));

		setApplicationDetail(applicationDetail.orElseThrow(() -> new IllegalArgumentException("No application found")));
	}

	private void setApplicationDetail(ApplicationDetail applicationDetail) {
		this.getExtension().setApplicationDetail(applicationDetail);
	}

	@Override
	public String getDescription() {
		return "Get the application detail from Cloud Foundry";
	}
}
