package io.pivotal.services.plugin.tasks.helper;

import java.util.List;

import io.pivotal.services.plugin.CfManifestUtil;
import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfRouteUtil;
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
            List<DecomposedRoute> routes = CfRouteUtil.decomposedRoutes(cfOperations,cfProperties.routes(),cfProperties.path());
            for(DecomposedRoute route: routes) {
                resp = resp.then(unmapRoute(cfOperations, cfProperties.name(), route.getHost(), route.getDomain(), route.getPort(), route.getPath()));
            }
        } else {
            resp = resp.then(unmapRoute(cfOperations, cfProperties.name(), cfProperties.domain(), cfProperties.host(), null, cfProperties.path()));
        }
        return resp;
    }

    private Mono<Void> unmapRoute(CloudFoundryOperations cfOperations, String appName, String host, String domain, Integer port, String path) {
        return cfOperations.routes()
            .unmap(UnmapRouteRequest
                .builder()
                .applicationName(appName)
                .host(host)
                .domain(domain)
                .port(port)
                .path(path)
                .build()).doOnSubscribe((s) -> {
                LOGGER.lifecycle("Unmapping hostname '{}' in domain '{}' with path '{}' of app '{}'", host,
                    domain, path, appName);
            });
    }
}
