package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.Applications;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfPushDelegateTest {

	private CfPushDelegate cfPushDelegate;

	@Before
	public void setUp() {
		this.cfPushDelegate = new CfPushDelegate();
	}

	@Test
	public void testCfPushWithoutEnvOrServices() throws Exception {
		CloudFoundryOperations cfOperations = mock(CloudFoundryOperations.class);
		Applications mockApplications = mock(Applications.class);
		when(cfOperations.applications()).thenReturn(mockApplications);
		when(mockApplications.push(any(PushApplicationRequest.class))).thenReturn(Mono.empty());
		when(mockApplications.restart(any(RestartApplicationRequest.class))).thenReturn(Mono.empty());



		CfProperties cfAppProperties = sampleApp();
		cfPushDelegate.push(cfOperations, cfAppProperties);
	}


	private CfProperties sampleApp() throws Exception {
		URL sampleFile = this.getClass().getResource("/sample.txt");
		File file = new File(sampleFile.toURI());
		return ImmutableCfProperties.builder()
				.ccHost("cchost")
				.ccUser("ccuser")
				.ccPassword("ccpassword")
				.org("org")
				.space("space")
				.name("name")
				.filePath(file.getPath())
				.build();
	}

}
