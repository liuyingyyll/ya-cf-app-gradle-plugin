//package io.pivotal.services.plugin.tasks;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import io.pivotal.services.plugin.CfPluginExtension;
//import org.gradle.api.Project;
//import org.gradle.testfixtures.ProjectBuilder;
//import org.junit.Before;
//import org.junit.Test;
//import static org.mockito.Mockito.*;
//
//import io.pivotal.services.plugin.tasks.*;
//
//public class CfPushTests {
//
//	private Project project;
//
//	@Before
//	public void before() {
//		this.project = ProjectBuilder.builder().build();
//		this.project.getPluginManager().apply("cf-app");
//
//		this.project.getExtensions().getByType(CfPluginExtension.class).setCcHost("host");
//		this.project.getExtensions().getByType(CfPluginExtension.class).setSpace("space");
//		this.project.getExtensions().getByType(CfPluginExtension.class).setOrg("org");
//		this.project.getExtensions().getByType(CfPluginExtension.class).setName("name");
//
//	}
//
//	@Test
//	public void testPushTask() {
//		CfPushTask pushTask = (CfPushTask)this.project.getTasks().getAt("cf-push");
//		pushTask.push();
//	}
//
//
//}