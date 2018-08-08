package io.pivotal.services.plugin.tasks.helper;

import java.util.List;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfRouteUtil;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DecomposedRoute;
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
        Mono<Void> resp = Mono.empty();
        if (cfProperties.routes() != null && !cfProperties.routes().isEmpty()) {
            List<DecomposedRoute> routes = CfRouteUtil.decomposedRoutes(cfOperations,cfProperties.routes(),cfProperties.path());
            for(DecomposedRoute route: routes) {
                resp = resp.then(mapRoute(cfOperations, cfProperties.name(), route.getHost(), route.getDomain(), route.getPort(), route.getPath()));
            }
        } else {
            resp = resp.then(mapRoute(cfOperations, cfProperties.name(), cfProperties.host(), cfProperties.domain(), null, cfProperties.path()));
        }

        return resp;
    }

    private Mono<Void> mapRoute(CloudFoundryOperations cfOperations, String appName, String host, String domain, Integer port, String path) {
        return cfOperations.routes()
            .map(MapRouteRequest
                .builder()
                .applicationName(appName)
                .host(host)
                .domain(domain)
                .port(port)
                .path(path)
                .build()).then().doOnSubscribe((s) -> {
                LOGGER.lifecycle("Mapping hostname '{}' in domain '{}' with path '{}' of app '{}'", host,
                    domain, path, appName);
            });
    }

}
