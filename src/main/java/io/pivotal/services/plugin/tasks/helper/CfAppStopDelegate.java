package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.StopApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfAppStopDelegate.class);

    public Mono<Void> stopApp(CloudFoundryOperations cfOperations,
                              CfProperties cfProperties) {

        return cfOperations.applications()
            .stop(StopApplicationRequest.builder().name(cfProperties.name()).build())
            .doOnSubscribe((s) -> {
                LOGGER.lifecycle("Stopping app '{}'", cfProperties.name());
            });

    }

}
