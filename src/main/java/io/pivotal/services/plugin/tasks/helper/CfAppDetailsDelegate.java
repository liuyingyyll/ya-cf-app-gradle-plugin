package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for getting the details of an app
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfAppDetailsDelegate.class);

	public Mono<Optional<ApplicationDetail>> getAppDetails(CloudFoundryOperations cfOperations,
														   CfAppProperties cfAppProperties) {

		LOGGER.lifecycle("Checking details of app '{}'", cfAppProperties.getName());
		Mono<ApplicationDetail> applicationDetailMono = cfOperations.applications()
				.get(
						GetApplicationRequest.builder()
								.name(cfAppProperties.getName())
								.build());

		return applicationDetailMono
				.map(appDetail -> Optional.ofNullable(appDetail))
				.otherwise(Exception.class, e -> Mono.just(Optional.empty()));
	}

}
