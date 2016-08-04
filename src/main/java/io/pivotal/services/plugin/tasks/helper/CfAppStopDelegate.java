package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import reactor.core.publisher.Mono;

/**
 * Helper responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopDelegate {

	public Mono<Void> stopApp(CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {

		 return cfOperations.applications()
				.stop(StopApplicationRequest.builder().name(cfAppProperties.getName()).build());

	}

}
