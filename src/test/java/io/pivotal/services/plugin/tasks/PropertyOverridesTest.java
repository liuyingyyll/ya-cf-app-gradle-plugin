package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
import io.pivotal.services.plugin.PropertyNameConstants;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyOverridesTest {

	@Test
	public void testPropertyOverridesForAppName() {
		Project project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("cf-push");
		setPropsInExtension((CfPushPluginExtension)project.getExtensions().getByName("cfConfig"));

		CfPushTask cfPushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		assertThat(cfPushTask.getCfApplicationName()).isEqualTo("name-fromplugin");
		assertThat(cfPushTask.getCcHost()).isEqualTo("cchost-fromplugin");
		assertThat(cfPushTask.getCcPassword()).isEqualTo("ccpassword-fromplugin");
		assertThat(cfPushTask.getBuildpack()).isEqualTo("buildpack-fromplugin");
		assertThat(cfPushTask.getOrg()).isEqualTo("org-fromplugin");
		assertThat(cfPushTask.getSpace()).isEqualTo("space-fromplugin");
		assertThat(cfPushTask.getInstances()).isEqualTo(3);
		assertThat(cfPushTask.getMemory()).isEqualTo(12);
	}

	private void setPropsInExtension(CfPushPluginExtension ext) {
		ext.setName("name-fromplugin");
		ext.setCcHost("cchost-fromplugin");
		ext.setCcPassword("ccpassword-fromplugin");
		ext.setBuildpack("buildpack-fromplugin");
		ext.setOrg("org-fromplugin");
		ext.setSpace("space-fromplugin");
		ext.setMemory(12);
		ext.setInstances(3);
	}

}
