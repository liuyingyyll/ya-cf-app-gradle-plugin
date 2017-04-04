package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper class for mapping a route
 *
 * @author Biju Kunjummen
 */
public class CfMapRouteDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfMapRouteDelegate.class);

    public Mono<Void> mapRoute(CloudFoundryOperations cfOperations,
                               CfProperties cfProperties) {

        Mono<Void> resp = cfOperations.routes()
            .map(MapRouteRequest
                .builder()
                .applicationName(cfProperties.name())
                .domain(cfProperties.domain())
                .host(cfProperties.hostName())
                .path(cfProperties.path()).build()).then().doOnSubscribe((s) -> {
                LOGGER.lifecycle("Mapping hostname '{}' in domain '{}' with path '{}' of app '{}'", cfProperties.hostName(),
                    cfProperties.domain(), cfProperties.path(), cfProperties.name());
            });

        return resp;

    }

}
