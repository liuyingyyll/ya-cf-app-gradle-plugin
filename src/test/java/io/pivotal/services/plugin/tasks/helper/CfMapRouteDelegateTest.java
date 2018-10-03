package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.domains.Domain;
import org.cloudfoundry.operations.domains.Domains;
import org.cloudfoundry.operations.domains.Status;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.Routes;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author Biju Kunjummen
 */
public class CfMapRouteDelegateTest {


    @Test
    public void mapRoutesNoExplicitRoutes() {
        CfMapRouteDelegate cfMapRouteDelegate = new CfMapRouteDelegate();
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        Routes mockRoutes = mock(Routes.class);
        when(mockRoutes.map(any(MapRouteRequest.class))).thenReturn(Mono.just(123));
        when(cfOperations.routes()).thenReturn(mockRoutes);
        CfProperties cfProperties = sampleApp(Collections.emptyList());
        Flux<Integer> result = cfMapRouteDelegate.mapRoute(cfOperations, cfProperties);
        StepVerifier.create(result)
            .expectNext(123)
            .expectComplete().verify();
    }

    @Test
    public void mapRoutesExplicitWithValidDomains() {
        CfMapRouteDelegate cfMapRouteDelegate = new CfMapRouteDelegate();
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        Routes mockRoutes = mock(Routes.class);
        when(mockRoutes.map(any(MapRouteRequest.class))).thenReturn(Mono.just(123));
        when(cfOperations.routes()).thenReturn(mockRoutes);
        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list())
            .thenReturn(
                Flux.just(
                    Domain.builder().id("id1").name("test1.domain").type("typ").status(Status.SHARED).build(),
                    Domain.builder().id("id2").name("test2.domain").type("typ").status(Status.SHARED).build()
                )
            );

        when(cfOperations.domains()).thenReturn(mockDomains);
        CfProperties cfProperties = sampleApp(Arrays.asList("app1.test1.domain", "app2.test2.domain"));
        Flux<Integer> result = cfMapRouteDelegate
            .mapRoute(cfOperations, cfProperties);
        StepVerifier.create(result)
            .expectNext(123)
            .expectNext(123)
            .expectComplete()
            .verify();
    }

    @Test
    public void mapRoutesExplicitWithInvalidDomains() {
        CfMapRouteDelegate cfMapRouteDelegate = new CfMapRouteDelegate();
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        Routes mockRoutes = mock(Routes.class);
        when(mockRoutes.map(any(MapRouteRequest.class))).thenReturn(Mono.just(123));
        when(cfOperations.routes()).thenReturn(mockRoutes);
        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list())
            .thenReturn(
                Flux.just(
                    Domain.builder().id("id1").name("test1.domain").type("typ").status(Status.SHARED).build(),
                    Domain.builder().id("id2").name("test2.domain").type("typ").status(Status.SHARED).build()
                )
            );

        when(cfOperations.domains()).thenReturn(mockDomains);
        CfProperties cfProperties = sampleApp(Arrays.asList("app1.test1.domain", "app2.invalid.domain"));
        Flux<Integer> result = cfMapRouteDelegate
            .mapRoute(cfOperations, cfProperties);
        StepVerifier.create(result)
            .expectError(IllegalArgumentException.class)
            .verify();
    }
    
    private CfProperties sampleApp(List<String> routes) {
        return ImmutableCfProperties.builder()
            .ccHost("cchost")
            .ccUser("ccuser")
            .ccPassword("ccpassword")
            .org("org")
            .space("space")
            .name("test")
            .domain("test.domain")
            .host("test")
            .routes(routes)
            .build();
    }
}
