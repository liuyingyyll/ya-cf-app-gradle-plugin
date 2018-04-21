package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for getting the environment variables of an app
 *
 * @author Lee Dobryden, Jonny Nabors
 */
public class CfAppEnvDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfAppEnvDelegate.class);

    public Mono<Optional<ApplicationEnvironments>> getAppEnv(
        CloudFoundryOperations cfOperations, CfProperties cfProperties) {
        Mono<ApplicationEnvironments> applicationEnvironmentsMono = cfOperations.applications()
            .getEnvironments(GetApplicationEnvironmentsRequest.builder().name(cfProperties.name()).build())
            .doOnSubscribe((c) -> {
                LOGGER.lifecycle("Checking environment of app '{}'", cfProperties.name());
            });

        return applicationEnvironmentsMono
            .map(applicationEnvironments -> Optional.ofNullable(applicationEnvironments))
            .onErrorResume(Exception.class, e -> Mono.just(Optional.empty()));
    }

}
