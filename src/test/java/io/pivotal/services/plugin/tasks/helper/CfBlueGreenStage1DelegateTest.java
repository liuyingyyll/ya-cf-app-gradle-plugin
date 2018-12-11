package io.pivotal.services.plugin.tasks.helper;


import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.cloudfoundry.operations.domains.Domain;
import org.cloudfoundry.operations.domains.Domains;
import org.cloudfoundry.operations.domains.Status;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Biju Kunjummen
 * @author Gabriel Couto
 */
public class CfBlueGreenStage1DelegateTest {

    @Mock
    private CfPushDelegate pushDelegate;

    @Mock
    private CfAppDetailsDelegate appDetailsDelegate;

    @Mock
    private CfAppEnvDelegate appEnvDelegate;

    @InjectMocks
    private CfBlueGreenStage1Delegate blueGreenStage1Delegate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStage1ExistingApp() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list()).thenReturn(Flux.just(Domain.builder().id("id").name("test.com").status(Status.SHARED).build()));
        when(cfOperations.domains()).thenReturn(mockDomains);
        CfProperties cfAppProperties = sampleApp();

        when(appDetailsDelegate.getAppDetails(any(CloudFoundryOperations.class), eq(cfAppProperties)))
            .thenReturn(Mono.just(Optional.of(ApplicationDetail.builder().
                name("test").instances(10)
                .runningInstances(2)
                .stack("stack")
                .diskQuota(500)
                .memoryLimit(100)
                .requestedState("start")
                .id("id")
                .build())));

        Map<String, String> existingEnvironment = new HashMap<>();
        existingEnvironment.put("userProp", "value");

        when(appEnvDelegate.getAppEnv(any(CloudFoundryOperations.class), eq(cfAppProperties)))
            .thenReturn(Mono.just(Optional.of(ApplicationEnvironments.builder().userProvided(existingEnvironment).build())));

        when(pushDelegate.push(any(CloudFoundryOperations.class), any(CfProperties.class)))
            .thenReturn(Mono.empty());

        Mono<Void> resultMono = this.blueGreenStage1Delegate.runStage1(project, cfOperations, cfAppProperties);
        StepVerifier.create(resultMono)
            .expectComplete()
            .verify(Duration.ofMillis(2000L));

        ArgumentCaptor<CfProperties> argumentCaptor = ArgumentCaptor.forClass(CfProperties.class);

        verify(this.pushDelegate).push(any(CloudFoundryOperations.class), argumentCaptor.capture());

        Map<String, String> expectedEnvironment = getUserEnvironment();

        CfProperties captured = argumentCaptor.getValue();
        assertThat(captured.name()).isEqualTo("test-green");
        assertThat(captured.host()).isEqualTo(null);
        assertThat(captured.domain()).isEqualTo(null);
        assertThat(captured.routes().get(0)).startsWith("route-green");
        assertThat(captured.instances()).isEqualTo(10);
        assertThat(captured.diskQuota()).isEqualTo(500);
        assertThat(captured.memory()).isEqualTo(100);
        assertThat(captured.environment()).isEqualTo(expectedEnvironment);
    }


    @Test
    public void testStage1NoExistingApp() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list()).thenReturn(Flux.just(Domain.builder().id("id").name("test.com").status(Status.SHARED).build()));
        when(cfOperations.domains()).thenReturn(mockDomains);

        CfProperties cfAppProperties = sampleApp();

        when(appDetailsDelegate.getAppDetails(any(CloudFoundryOperations.class), eq(cfAppProperties)))
            .thenReturn(Mono.just(Optional.empty()));

        when(appEnvDelegate.getAppEnv(any(CloudFoundryOperations.class), eq(cfAppProperties)))
            .thenReturn(Mono.just(Optional.empty()));

        when(pushDelegate.push(any(CloudFoundryOperations.class), any(CfProperties.class)))
            .thenReturn(Mono.empty());

        Mono<Void> resultMono = this.blueGreenStage1Delegate.runStage1(project, cfOperations, cfAppProperties);
        StepVerifier.create(resultMono)
            .expectComplete()
            .verify(Duration.ofMillis(2000L));

        ArgumentCaptor<CfProperties> argumentCaptor = ArgumentCaptor.forClass(CfProperties.class);

        verify(this.pushDelegate).push(any(CloudFoundryOperations.class), argumentCaptor.capture());

        CfProperties captured = argumentCaptor.getValue();
        assertThat(captured.name()).isEqualTo("test-green");
        assertThat(captured.host()).isEqualTo(null);
        assertThat(captured.domain()).isEqualTo(null);
        assertThat(captured.routes().get(0)).startsWith("route-green");

        assertThat(captured.instances()).isEqualTo(2);
        assertThat(captured.diskQuota()).isNull();
        assertThat(captured.memory()).isNull();
        assertThat(captured.environment()).isEqualTo(getUserEnvironment());

    }

    @Test
    public void testStage1NoExistingAppAndNoHost() {
        Project project = mock(Project.class);
        CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
        Domains mockDomains = mock(Domains.class);
        when(mockDomains.list()).thenReturn(Flux.just(Domain.builder().id("id").name("test.com").status(Status.SHARED).build()));
        when(cfOperations.domains()).thenReturn(mockDomains);

        CfProperties cfAppProperties = sampleAppNoHost();

        when(appDetailsDelegate.getAppDetails(any(CloudFoundryOperations.class), eq(cfAppProperties)))
            .thenReturn(Mono.just(Optional.empty()));

        when(appEnvDelegate.getAppEnv(any(CloudFoundryOperations.class), eq(cfAppProperties)))
            .thenReturn(Mono.just(Optional.empty()));

        when(pushDelegate.push(any(CloudFoundryOperations.class), any(CfProperties.class)))
            .thenReturn(Mono.empty());

        Mono<Void> resultMono = this.blueGreenStage1Delegate.runStage1(project, cfOperations, cfAppProperties);
        StepVerifier.create(resultMono)
            .expectComplete()
            .verify(Duration.ofMillis(2000L));

        ArgumentCaptor<CfProperties> argumentCaptor = ArgumentCaptor.forClass(CfProperties.class);

        verify(this.pushDelegate).push(any(CloudFoundryOperations.class), argumentCaptor.capture());

        CfProperties captured = argumentCaptor.getValue();
        assertThat(captured.name()).isEqualTo("test-green");
        assertThat(captured.host()).isEqualTo(null);
        assertThat(captured.domain()).isEqualTo(null);
        assertThat(captured.routes().get(0)).startsWith("green.");

        assertThat(captured.instances()).isEqualTo(2);
        assertThat(captured.diskQuota()).isNull();
        assertThat(captured.memory()).isNull();
        assertThat(captured.environment()).isEqualTo(getUserEnvironment());

    }

    private CfProperties sampleApp() {
        HashMap<String, String> environment = getUserEnvironment();
        return ImmutableCfProperties.builder()
            .ccHost("cchost")
            .ccUser("ccuser")
            .ccPassword("ccpassword")
            .org("org")
            .space("space")
            .name("test")
            .instances(2)
            .host("route")
            .environment(environment)
            .build();
    }

    private CfProperties sampleAppNoHost() {
        HashMap<String, String> environment = getUserEnvironment();
        return ImmutableCfProperties.builder()
            .ccHost("cchost")
            .ccUser("ccuser")
            .ccPassword("ccpassword")
            .org("org")
            .space("space")
            .name("test")
            .instances(2)
//            .host("")
            .environment(environment)
            .build();
    }

    private HashMap<String, String> getUserEnvironment() {
        HashMap<String, String> environment = new HashMap<>();
        environment.put("property", "value");
        return environment;
    }
}
