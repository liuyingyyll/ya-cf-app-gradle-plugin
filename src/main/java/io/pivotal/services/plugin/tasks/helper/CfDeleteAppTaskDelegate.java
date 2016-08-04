package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for Deleting an app, the logic has been centralized here as it is going to get called from
 * multiple places
 *
 * @author Biju Kunjummen
 */
public class CfDeleteAppTaskDelegate{

	public Mono<Void> deleteApp(CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {
		return cfOperations.applications().delete(
				DeleteApplicationRequest
						.builder()
						.name(cfAppProperties.getName())
						.build());
	}
}
