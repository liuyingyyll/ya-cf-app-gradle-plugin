package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ScaleApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper class for scaling instance Count of an app
 *
 * @author Biju Kunjummen
 */
public class CfScaleInstanceCountDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfScaleInstanceCountDelegate.class);

	public Mono<Void> scaleInstances(CloudFoundryOperations cfOperations,
									 CfProperties cfProperties, int instanceCount) {

		LOGGER.lifecycle("Scaling app {} to instance count {}", cfProperties.name(), instanceCount);
		Mono<Void> resp = cfOperations.applications().scale(
				ScaleApplicationRequest.builder()
						.instances(instanceCount)
						.build()
		);

		return resp;

	}

}
