package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Responsible for renaming an app. Since this functionality is going to be called from a few places this logic has been
 * centralized here.
 *
 * @author Biju Kunjummen
 */
public class CfRenameAppDelegate {
	private static final Logger LOGGER = Logging.getLogger(CfRenameAppDelegate.class);

	public Mono<Void> renameApp(CloudFoundryOperations cfOperations,
								CfProperties cfOldAppProperties, CfProperties cfNewProperties) {

		if (cfNewProperties.name() == null) {
			throw new RuntimeException("New name not provided");
		}
		LOGGER.lifecycle("Renaming app from {} to {}", cfOldAppProperties.name(), cfNewProperties.name());

		return cfOperations.applications().rename(RenameApplicationRequest
				.builder()
				.name(cfOldAppProperties.name())
				.newName(cfNewProperties.name())
				.build());
	}

}
