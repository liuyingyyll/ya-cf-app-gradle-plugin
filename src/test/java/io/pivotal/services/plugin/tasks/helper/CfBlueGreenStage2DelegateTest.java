package io.pivotal.services.plugin.tasks.helper;


import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.domains.Domain;
import org.cloudfoundry.operations.domains.Domains;
import org.cloudfoundry.operations.domains.Status;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CfBlueGreenStage2DelegateTest {

    @InjectMocks
    private CfBlueGreenStage2Delegate blueGreenStage2Delegate;

    @Mock
    private CfMapRouteDelegate mapRouteDelegate;

    @Mock
    private CfUnMapRouteDelegate unMapRouteDelegate;

    @Mock
    private CfAppDetailsDelegate detailsTaskDelegate;

    @Mock
    private CfRenameAppDelegate renameAppTaskDelegate;

    @Mock
    private CfDeleteAppDelegate deleteAppTaskDelegate;

    @Mock
    private CfAppStopDelegate stopDelegate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStage2NoExistingApp() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list()).thenReturn(Flux.just(Domain.builder().id("id").name("test.com").status(Status.SHARED).build()));
        when(cfOperations.domains()).thenReturn(mockDomains);



        when(detailsTaskDelegate.getAppDetails(any(CloudFoundryOperations.class), any(CfProperties.class)))
            .thenReturn(Mono.just(Optional.empty()));

        when(this.mapRouteDelegate.mapRoute(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Flux.empty());
        when(this.unMapRouteDelegate.unmapRoute(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Flux.empty());
        when(this.renameAppTaskDelegate.renameApp(any(CloudFoundryOperations.class), any(CfProperties.class), any(CfProperties.class))).thenReturn(Mono.empty());
        when(this.deleteAppTaskDelegate.deleteApp(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Mono.empty());
        when(this.stopDelegate.stopApp(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Mono.empty());

        Mono<Void> resp = this.blueGreenStage2Delegate.runStage2(project, cfOperations, cfAppProperties);
        StepVerifier.create(resp)
            .expectComplete()
            .verify(Duration.ofMillis(2000L));

        //delete of old backup should not be called..there is no backup app..
        verify(this.deleteAppTaskDelegate, times(0)).deleteApp(any(CloudFoundryOperations.class), any(CfProperties.class));

        //mapping correct route to green
        verify(this.mapRouteDelegate, times(1)).mapRoute(any(CloudFoundryOperations.class), any(CfProperties.class));

        //unmapping green route from green app
        verify(this.unMapRouteDelegate, times(1)).unmapRoute(any(CloudFoundryOperations.class), any(CfProperties.class));

        //rename green to app
        verify(this.renameAppTaskDelegate, times(1)).renameApp(any(CloudFoundryOperations.class), any(CfProperties.class), any(CfProperties.class));
    }

    @Test
    public void testStage2ExistingAppAndBackup() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list()).thenReturn(Flux.just(Domain.builder().id("id").name("test.com").status(Status.SHARED).build()));
        when(cfOperations.domains()).thenReturn(mockDomains);

        when(detailsTaskDelegate.getAppDetails(any(CloudFoundryOperations.class), any(CfProperties.class)))
            .thenReturn(Mono.just(Optional.of(sampleApplicationDetail())));

        when(this.mapRouteDelegate.mapRoute(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Flux.empty());
        when(this.unMapRouteDelegate.unmapRoute(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Flux.empty());
        when(this.renameAppTaskDelegate.renameApp(any(CloudFoundryOperations.class), any(CfProperties.class), any(CfProperties.class))).thenReturn(Mono.empty());
        when(this.deleteAppTaskDelegate.deleteApp(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Mono.empty());
        when(this.stopDelegate.stopApp(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Mono.empty());

        Mono<Void> resp = this.blueGreenStage2Delegate.runStage2(project, cfOperations, cfAppProperties);
        StepVerifier.create(resp)
            .expectComplete()
            .verify(Duration.ofMillis(2000L));

        //delete of old backup
        verify(this.deleteAppTaskDelegate, times(1)).deleteApp(any(CloudFoundryOperations.class), any(CfProperties.class));

        //mapping correct route to green
        verify(this.mapRouteDelegate, times(1)).mapRoute(any(CloudFoundryOperations.class), any(CfProperties.class));

        //unmapping green route from green app
        //unmapping route from existing app
        verify(this.unMapRouteDelegate, times(2)).unmapRoute(any(CloudFoundryOperations.class), any(CfProperties.class));

        //rename green to app
        //rename existing app to blue
        verify(this.renameAppTaskDelegate, times(2)).renameApp(any(CloudFoundryOperations.class), any(CfProperties.class), any(CfProperties.class));
    }

    private ApplicationDetail sampleApplicationDetail() {
        return ApplicationDetail.builder()
            .id("id")
            .instances(1)
            .name("test")
            .stack("stack")
            .diskQuota(1)
            .requestedState("started")
            .memoryLimit(1000)
            .runningInstances(2)
            .build();
    }

    private CfProperties sampleApp() {
        return ImmutableCfProperties.builder()
            .ccHost("cchost")
            .ccUser("ccuser")
            .ccPassword("ccpassword")
            .org("org")
            .space("space")
            .name("test")
            .host("test")
            .build();
    }

}
