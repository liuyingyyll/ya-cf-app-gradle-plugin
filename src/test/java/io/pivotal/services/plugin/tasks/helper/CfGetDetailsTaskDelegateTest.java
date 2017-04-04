package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CfGetDetailsTaskDelegateTest {

    private CfAppDetailsDelegate detailsTaskDelegate = new CfAppDetailsDelegate();


    @Test
    public void testGetDetailsNoException() {
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        ApplicationDetail appDetail = sampleApplicationDetail();

        Applications mockApplications = mock(Applications.class);
        when(cfOperations.applications()).thenReturn(mockApplications);

        when(mockApplications.get(any(GetApplicationRequest.class))).thenReturn(Mono.just(appDetail));

        Mono<Optional<ApplicationDetail>> appDetailsMono = detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties);

        assertThat(appDetailsMono.block().isPresent()).isTrue();
    }

    @Test
    public void testGetDetailsNoApplication() {
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        Applications mockApplications = mock(Applications.class);
        when(cfOperations.applications()).thenReturn(mockApplications);

        when(mockApplications.get(any(GetApplicationRequest.class))).thenReturn(Mono.error(new RuntimeException("No such app")));

        Mono<Optional<ApplicationDetail>> appDetailsMono = detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties);

        Optional<ApplicationDetail> appDetailOptional = appDetailsMono.block();

        assertThat(appDetailOptional.isPresent()).isFalse();
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
            .hostName("test")
            .build();
    }


}
