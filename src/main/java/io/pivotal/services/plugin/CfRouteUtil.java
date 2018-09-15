package io.pivotal.services.plugin;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.DecomposedRoute;
import org.cloudfoundry.operations.applications.DomainSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Gabriel Couto
 * @author Biju Kunjummen
 */
public class CfRouteUtil {

    /**
     * Returns a list of decomposed routes
     *
     * @param cfOperations the operations to check the domain names against
     * @param routes       all aplication's routes
     * @param routePath    the application path to be included in the routes
     * @return the decomposed routes
     */
    public static Mono<List<DecomposedRoute>> decomposedRoutes(CloudFoundryOperations cfOperations, List<String> routes, String routePath) {
        final Mono<List<DomainSummary>> domainSummariesMono = fetchDomainSummaries(cfOperations);
        
        List<Mono<DecomposedRoute>> decomposedRoutes = routes.stream().map(route ->
            domainSummariesMono.flatMap(domainSummaries -> decomposeRoute(domainSummaries, route, routePath))
        ).collect(Collectors.toList());

        return Flux.merge(decomposedRoutes).collectList();
    }

    /**
     * Get the first route and generate a temp route from it, adding the suffix
     * after the hostname.
     *
     * @param cfOperations the operations to check the domain names against
     * @param cfProperties the properties of the app
     * @param suffix       the suffix to add in the host
     * @return the calculated route
     */
    public static Mono<String> getTempRoute(CloudFoundryOperations cfOperations, CfProperties cfProperties, String suffix) {
        if (cfProperties.routes() == null || cfProperties.routes().isEmpty())
            return getTempRoute(cfProperties.host(), cfProperties.domain(), null, cfProperties.path(), suffix);
        Mono<DecomposedRoute> routeMono = fetchDomainSummaries(cfOperations).flatMap(domainSummaries -> decomposeRoute(domainSummaries, cfProperties.routes().get(0), cfProperties.path()));
        return routeMono.flatMap(route -> getTempRoute(route.getHost(), route.getDomain(), route.getPort(), route.getPath(), suffix));
    }

    private static Mono<String> getTempRoute(String host, String domain, Integer port, String path, String suffix) {
        return Mono.just((host != null ? host + suffix + "." : suffix + ".")
            + domain
            + (port != null ? ":" + port : "")
            + (path != null ? "/" + path : ""));
    }



    public static Mono<List<DomainSummary>> fetchDomainSummaries(CloudFoundryOperations cfOperations) {
        return cfOperations.domains().list()
            .map(domain -> DomainSummary.builder()
                .id(domain.getId())
                .name(domain.getName())
                .type(domain.getType())
                .build())
            .collectList();
    }

    /**
     * Copy of org.cloudfoundry.operations.applications.RouteUtil.decomposeRoute
     */
    private static Mono<DecomposedRoute> decomposeRoute(List<DomainSummary> availableDomains, String route, String routePath) {
        String domain = null;
        String host = null;
        String path = null;
        Integer port = null;
        String routeWithoutSuffix = route;

        if (availableDomains.size() == 0) {
            throw new IllegalArgumentException(String.format("The route %s did not match any existing domains", route));
        }

        List<DomainSummary> sortedDomains = availableDomains.stream()
            .sorted(Comparator.<DomainSummary>comparingInt(domainSummary -> domainSummary.getName().length()).reversed())
            .collect(Collectors.toList());

        if (route.contains("/")) {
            int index = route.indexOf("/");
            path = routePath != null ? routePath : route.substring(index);
            routeWithoutSuffix = route.substring(0, index);
        } else if (hasPort(route)) {
            port = getPort(route);
            routeWithoutSuffix = route.substring(0, route.indexOf(":"));
        }

        for (DomainSummary item : sortedDomains) {
            if (isDomainMatch(routeWithoutSuffix, item.getName())) {
                domain = item.getName();
                if (domain.length() < routeWithoutSuffix.length()) {
                    host = routeWithoutSuffix.substring(0, routeWithoutSuffix.lastIndexOf(domain) - 1);
                }
                break;
            }
        }

        if (domain == null) {
            throw new IllegalArgumentException(String.format("The route %s did not match any existing domains", route));
        }

        if ((host != null || path != null) && port != null) {
            throw new IllegalArgumentException(String.format("The route %s is invalid: Host/path cannot be set with port", route));
        }

        return Mono.just(DecomposedRoute.builder()
            .domain(domain)
            .host(host)
            .path(path)
            .port(port)
            .build());
    }

    private static Integer getPort(String route) {
        Pattern pattern = Pattern.compile(":\\d+$");
        Matcher matcher = pattern.matcher(route);

        matcher.find();
        return Integer.valueOf(route.substring(matcher.start() + 1, matcher.end()));
    }

    private static Boolean hasPort(String route) {
        Pattern pattern = Pattern.compile("^.+?:\\d+$");
        Matcher matcher = pattern.matcher(route);

        return matcher.matches();
    }

    private static boolean isDomainMatch(String route, String domain) {
        return route.equals(domain) || route.endsWith(domain) && route.charAt(route.length() - domain.length() - 1) == '.';
    }
}
