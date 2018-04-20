package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.applications.GetApplicationEnvironmentsRequest;
import org.cloudfoundry.operations.applications.GetApplicationRequest;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfAppEnvDelegateTest {

    private CfAppEnvDelegate appEnvDelegate = new CfAppEnvDelegate();


    @Test
    public void testGetEnvironmentsNoException() {
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        ApplicationEnvironments appEnvironment = sampleAppEnvironment();

        Applications mockApplications = mock(Applications.class);
        when(cfOperations.applications()).thenReturn(mockApplications);

        when(mockApplications.getEnvironments(any(GetApplicationEnvironmentsRequest.class))).thenReturn(Mono.just(appEnvironment));

        Mono<Optional<ApplicationEnvironments>> appEnvMono = appEnvDelegate.getAppEnv(cfOperations, cfAppProperties);

        assertThat(appEnvMono.block().isPresent()).isTrue();
    }

    @Test
    public void testGetEnvironmentsNoApplication() {
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        CfProperties cfAppProperties = sampleApp();

        Applications mockApplications = mock(Applications.class);
        when(cfOperations.applications()).thenReturn(mockApplications);

        when(mockApplications.getEnvironments(any(GetApplicationEnvironmentsRequest.class)))
            .thenReturn(Mono.error(new RuntimeException("No such Environment")));

        Mono<Optional<ApplicationEnvironments>> appDetailsMono = appEnvDelegate.getAppEnv(cfOperations, cfAppProperties);

        Optional<ApplicationEnvironments> appDetailOptional = appDetailsMono.block();

        assertThat(appDetailOptional.isPresent()).isFalse();
    }


    private ApplicationEnvironments sampleAppEnvironment() {
        Map<String, String> userVars = new HashMap<>();
        userVars.put("prop", "value");

        return ApplicationEnvironments.builder()
            .userProvided(userVars)
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