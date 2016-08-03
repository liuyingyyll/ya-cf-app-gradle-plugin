package io.pivotal.services.plugin.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for getting the details of an app
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsTaskDelegate {

	public Mono<Optional<ApplicationDetail>> getAppDetails(CloudFoundryOperations cfOperations,
														   CfAppProperties cfAppProperties) {

		return cfOperations.applications()
				.get(
						GetApplicationRequest.builder()
								.name(cfAppProperties.getName())
								.build())
				.map(appDetail -> Optional.ofNullable(appDetail))
				.otherwise(IllegalArgumentException.class, ex -> Mono.just(Optional.empty()));
	}

}
