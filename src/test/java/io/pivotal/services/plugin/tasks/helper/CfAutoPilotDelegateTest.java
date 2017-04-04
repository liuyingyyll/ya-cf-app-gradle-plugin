package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class CfAutoPilotDelegateTest {

    @InjectMocks
    private CfAutoPilotDelegate cfAutoPilotTask;

    @Mock
    private CfPushDelegate cfPushDelegate;

    @Mock
    private CfRenameAppDelegate cfRenameAppDelegate;

    @Mock
    private CfDeleteAppDelegate deleteDelegate;

    @Mock
    private CfAppDetailsDelegate detailsTaskDelegate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCfAutoPilotExistingAppShouldCallRenameAndDelete() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        ApplicationDetail appDetail = sampleApplicationDetail();
        when(detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties))
            .thenReturn(Mono.just(Optional.of(appDetail)));

        when(cfRenameAppDelegate.renameApp(any(CloudFoundryOperations.class),
            any(CfProperties.class),
            any(CfProperties.class))).thenReturn(Mono.empty());

        when(cfPushDelegate.push(any(CloudFoundryOperations.class), any(CfProperties.class))).thenReturn(Mono.empty());
        when(deleteDelegate.deleteApp(any(CloudFoundryOperations.class),
            any(CfProperties.class))).thenReturn(Mono.empty());

        Mono<Void> resp = this.cfAutoPilotTask.runAutopilot(project, cfOperations, cfAppProperties);

        resp.block();

//		TestSubscriber.subscribe(resp).assertComplete();

        verify(cfRenameAppDelegate, times(1)).renameApp(any(CloudFoundryOperations.class),
            any(CfProperties.class), any(CfProperties.class));

        verify(cfPushDelegate, times(1)).push(any(CloudFoundryOperations.class), any(CfProperties.class));

        verify(deleteDelegate, times(1)).deleteApp(any(CloudFoundryOperations.class), any(CfProperties.class));
    }

    @Test
    public void testAutopilotNewAppShouldCallPushAlone() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        when(detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties)).thenReturn(Mono.just(Optional.empty()));

        when(cfPushDelegate.push(cfOperations, cfAppProperties)).thenReturn(Mono.empty());

        Mono<Void> resp = this.cfAutoPilotTask.runAutopilot(project, cfOperations, cfAppProperties);

        resp.block();
//		TestSubscriber.subscribe(resp).assertComplete();

        verify(cfRenameAppDelegate, times(0)).renameApp(any(CloudFoundryOperations.class),
            any(CfProperties.class), any(CfProperties.class));

        verify(cfPushDelegate, times(1)).push(any(CloudFoundryOperations.class), any(CfProperties.class));

        verify(deleteDelegate, times(0)).deleteApp(any(CloudFoundryOperations.class), any(CfProperties.class));
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
            .name("name")
            .build();
    }
}
