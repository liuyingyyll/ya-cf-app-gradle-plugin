package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.helper.*;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.TestSubscriber;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class CfAutoPilotTaskDelegateTest {

	@InjectMocks
	private CfAutoPilotTaskDelegate cfAutoPilotTask;

	@Mock
	private CfPushTaskDelegate cfPushTaskDelegate;

	@Mock
	private CfRenameAppTaskDelegate cfRenameAppTaskDelegate;

	@Mock
	private CfDeleteAppTaskDelegate deleteDelegate;

	@Mock
	private CfAppDetailsTaskDelegate detailsTaskDelegate;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCfAutoPilotExistingAppShouldCallRenameAndDelete() {
		Project project = mock(Project.class);
		CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
		CfAppProperties cfAppProperties = CfAppProperties.builder().name("test").build();

		ApplicationDetail appDetail = sampleApplicationDetail();
		when(detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties))
				.thenReturn(Mono.just(Optional.of(appDetail)));

		when(cfRenameAppTaskDelegate.renameApp(any(CloudFoundryOperations.class),
				any(CfAppProperties.class),
				any(CfAppProperties.class))).thenReturn(Mono.empty());

		when(cfPushTaskDelegate.push(cfOperations, cfAppProperties)).thenReturn(Mono.empty());
		when(deleteDelegate.deleteApp(any(CloudFoundryOperations.class),
				any(CfAppProperties.class))).thenReturn(Mono.empty());

		Mono<Void> resp = this.cfAutoPilotTask.runAutopilot(project, cfOperations, cfAppProperties);

		TestSubscriber.subscribe(resp).assertComplete();

		verify(cfRenameAppTaskDelegate, times(1)).renameApp(any(CloudFoundryOperations.class),
				any(CfAppProperties.class), any(CfAppProperties.class));

		verify(cfPushTaskDelegate, times(1)).push(any(CloudFoundryOperations.class), any(CfAppProperties.class));

		verify(deleteDelegate, times(1)).deleteApp(any(CloudFoundryOperations.class), any(CfAppProperties.class));
	}

	@Test
	public void testAutopilotNewAppShouldCallPushAlone() {
		Project project = mock(Project.class);
		CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
		CfAppProperties cfAppProperties = CfAppProperties.builder().name("test").build();

		when(detailsTaskDelegate.getAppDetails(cfOperations, cfAppProperties)).thenReturn(Mono.just(Optional.empty()));

		when(cfPushTaskDelegate.push(cfOperations, cfAppProperties)).thenReturn(Mono.empty());

		Mono<Void> resp = this.cfAutoPilotTask.runAutopilot(project, cfOperations, cfAppProperties);

		TestSubscriber.subscribe(resp).assertComplete();

		verify(cfRenameAppTaskDelegate, times(0)).renameApp(any(CloudFoundryOperations.class),
				any(CfAppProperties.class), any(CfAppProperties.class));

		verify(cfPushTaskDelegate, times(1)).push(any(CloudFoundryOperations.class), any(CfAppProperties.class));

		verify(deleteDelegate, times(0)).deleteApp(any(CloudFoundryOperations.class), any(CfAppProperties.class));
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
}
