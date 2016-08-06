package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RenameApplicationRequest;
import reactor.core.publisher.Mono;

/**
 * Responsible for renaming an app. Since this functionality is going to be called from a few places this logic has been
 * centralized here.
 *
 * @author Biju Kunjummen
 */
public class CfRenameAppDelegate {

	public Mono<Void> renameApp(CloudFoundryOperations cfOperations,
						  CfAppProperties cfOldAppProperties, CfAppProperties cfNewAppProperties) {

		if (cfNewAppProperties.getName() == null) {
			throw new RuntimeException("New name not provided");
		}

		return cfOperations.applications().rename(RenameApplicationRequest
				.builder()
				.name(cfOldAppProperties.getName())
				.newName(cfNewAppProperties.getName())
				.build());
	}

}
