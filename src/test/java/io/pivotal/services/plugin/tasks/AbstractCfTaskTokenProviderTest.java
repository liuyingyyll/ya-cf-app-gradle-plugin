package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class AbstractCfTaskTokenProviderTest {
	private Project project;

	@Before
	public void setUp() {
		this.project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("cf-app");
	}

	@Test(expected = IllegalStateException.class)
	public void tokenProviderShouldThrowExceptionIfTokenOrCredsNotSet() {
		CfPushTask pushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfProperties cfProperties = ImmutableCfProperties.builder()
			.name("name")
			.org("org")
			.space("space")
			.ccHost("host")
			.build();
		pushTask.getTokenProvider(cfProperties);
	}

	@Test
	public void noExceptionIfUserPasswordIsSet() {
		CfPushTask pushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfProperties cfProperties = ImmutableCfProperties.builder()
			.name("name")
			.org("org")
			.space("space")
			.ccHost("host")
			.ccUser("ccuser")
			.ccPassword("pass")
			.build();
		pushTask.getTokenProvider(cfProperties);
	}

	@Test
	public void noExceptionIfTokenIsSet() {
		CfPushTask pushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfProperties cfProperties = ImmutableCfProperties.builder()
			.name("name")
			.org("org")
			.space("space")
			.ccHost("host")
			.ccToken("token")
			.build();
		pushTask.getTokenProvider(cfProperties);
	}
}

