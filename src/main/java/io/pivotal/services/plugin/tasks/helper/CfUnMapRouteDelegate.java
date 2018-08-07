package io.pivotal.services.plugin.tasks.helper;

import java.util.List;

import io.pivotal.services.plugin.CfManifestUtil;
import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DecomposedRoute;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Helper responsible for unmapping a route
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfUnMapRouteDelegate.class);

    public Mono<Void> unmapRoute(CloudFoundryOperations cfOperations, CfProperties cfProperties) {
        Mono<Void> resp = Mono.empty();
        if (cfProperties.routes() != null && !cfProperties.routes().isEmpty()) {
            List<DecomposedRoute> routes = CfManifestUtil.decomposedRoutes(cfProperties.routes());
            for(DecomposedRoute route: routes) {
                resp = resp.then(cfOperations.routes()
                    .unmap(UnmapRouteRequest
                        .builder()
                        .applicationName(cfProperties.name())
                        .domain(route.getDomain())
                        .host(route.getHost())
                        .path(route.getPath())
                        .build()).then().doOnSubscribe((s) -> {
                        LOGGER.lifecycle("Unmapping hostname '{}' in domain '{}' with path '{}' of app '{}'", route.getHost(),
                            route.getDomain(), route.getPath(), cfProperties.name());
                    }));
            }
        } else {
            resp = cfOperations.routes()
                .unmap(UnmapRouteRequest
                    .builder()
                    .applicationName(cfProperties.name())
                    .domain(cfProperties.domain())
                    .host(cfProperties.host())
                    .path(cfProperties.path())
                    .build()).doOnSubscribe((s) -> {
                    LOGGER.lifecycle("Unmapping hostname '{}' in domain '{}' with path '{}' of app '{}'", cfProperties.host(),
                        cfProperties.domain(), cfProperties.path(), cfProperties.name());
                });
        }
        return resp;
    }
}
