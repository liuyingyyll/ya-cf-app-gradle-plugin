package io.pivotal.services.plugin.tasks.helper;


import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CfBlueGreenStage1DelegateTest {

	@Mock
	private CfPushDelegate pushDelegate;

	@Mock
	private CfAppDetailsDelegate appDetailsDelegate;

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
		CfProperties cfAppProperties = sampleApp();

		when(appDetailsDelegate.getAppDetails(any(CloudFoundryOperations.class), eq(cfAppProperties)))
				.thenReturn(Mono.just(Optional.of(ApplicationDetail.builder().
						name("test").instances(2)
						.runningInstances(2)
						.stack("stack")
						.diskQuota(500)
						.memoryLimit(100)
						.requestedState("start")
						.id("id")
						.build())));

		this.blueGreenStage1Delegate.runStage1(project, cfOperations, cfAppProperties);

		ArgumentCaptor<CfProperties> argumentCaptor = ArgumentCaptor.forClass(CfProperties.class);

		verify(this.pushDelegate).push(any(CloudFoundryOperations.class), argumentCaptor.capture());

		CfProperties captured = argumentCaptor.getValue();
		assertThat(captured.name()).isEqualTo("test-green");
		assertThat(captured.hostName()).isEqualTo("route-green");
	}

	private CfProperties sampleApp() {
		return ImmutableCfProperties.builder()
				.ccHost("cchost")
				.ccUser("ccuser")
				.ccPassword("ccpassword")
				.org("org")
				.space("space")
				.name("test")
				.instances(2)
				.hostName("route")
				.build();
	}
}
