package io.pivotal.services.plugin.tasks.helper;


import io.pivotal.services.plugin.CfAppProperties;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

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
		CfAppProperties cfAppProperties = CfAppProperties
				.builder()
				.name("test")
				.hostName("route")
				.build();

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

		ArgumentCaptor<CfAppProperties> argumentCaptor = ArgumentCaptor.forClass(CfAppProperties.class);

		verify(this.pushDelegate).push(any(CloudFoundryOperations.class), argumentCaptor.capture());

		CfAppProperties captured = argumentCaptor.getValue();
		assertThat(captured.getName()).isEqualTo("test-green");
		assertThat(captured.getHostName()).isEqualTo("route-green");
	}

}
