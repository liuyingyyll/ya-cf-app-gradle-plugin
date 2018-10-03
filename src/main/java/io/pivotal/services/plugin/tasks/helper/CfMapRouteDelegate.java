package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfRouteUtil;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DecomposedRoute;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Helper class for mapping a route
 *
 * @author Biju Kunjummen
 * @author Gabriel Couto
 */
public class CfMapRouteDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfMapRouteDelegate.class);

    public Flux<Integer> mapRoute(CloudFoundryOperations cfOperations,
                                  CfProperties cfProperties) {
        if (cfProperties.routes() != null && !cfProperties.routes().isEmpty()) {
            Mono<List<DecomposedRoute>> decomposedRoutes = CfRouteUtil.decomposedRoutes(cfOperations, cfProperties.routes(), cfProperties.path());

            return decomposedRoutes
                .flatMapMany(Flux::fromIterable)
                .flatMap(route ->
                    mapRoute(cfOperations, cfProperties.name(), route.getHost(), route.getDomain(), route.getPort(), route.getPath()));

        } else {
            return mapRoute(cfOperations, cfProperties.name(), cfProperties.host(), cfProperties.domain(), null, cfProperties.path()).flux();
        }
    }

    private Mono<Integer> mapRoute(CloudFoundryOperations cfOperations, String appName, String host, String domain, Integer port, String path) {
        return cfOperations.routes()
            .map(MapRouteRequest
                .builder()
                .applicationName(appName)
                .host(host)
                .domain(domain)
                .port(port)
                .path(path)
                .build()).doOnSubscribe((s) -> {
                LOGGER.lifecycle("Mapping hostname '{}' in domain '{}' with path '{}' of app '{}'", host,
                    domain, path, appName);
            });
    }

}