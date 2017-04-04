package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Responsible for Deleting an app, the logic has been centralized here as it is going to get called from
 * multiple places
 *
 * @author Biju Kunjummen
 */
public class CfDeleteAppDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfDeleteAppDelegate.class);

    public Mono<Void> deleteApp(CloudFoundryOperations cfOperations,
                                CfProperties cfProperties) {
        return cfOperations.applications()
            .delete(DeleteApplicationRequest.builder().name(cfProperties.name()).build())
            .doOnSubscribe((s) -> {
                LOGGER.lifecycle("About to delete App '{}'", cfProperties.name());
            });
    }
}
