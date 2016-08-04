package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppPluginExtension;
import io.pivotal.services.plugin.CfAppProperties;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyOverridesTest {

	@Test
	public void testPropertyOverridesForAppName() {
		Project project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("cf-app");
		setPropsInExtension((CfAppPluginExtension)project.getExtensions().getByName("cfConfig"));

		CfPushTask cfPushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfAppProperties props = cfPushTask.getCfAppProperties();
		assertThat(props.getName()).isEqualTo("name-fromplugin");
		assertThat(props.getCcHost()).isEqualTo("cchost-fromplugin");
		assertThat(props.getCcPassword()).isEqualTo("ccpassword-fromplugin");
		assertThat(props.getBuildpack()).isEqualTo("buildpack-fromplugin");
		assertThat(props.getOrg()).isEqualTo("org-fromplugin");
		assertThat(props.getSpace()).isEqualTo("space-fromplugin");
		assertThat(props.getInstances()).isEqualTo(3);
		assertThat(props.getMemory()).isEqualTo(12);
		assertThat(props.getStagingTimeout()).isEqualTo(15);
		assertThat(props.getStartupTimeout()).isEqualTo(5);
	}


	private void setPropsInExtension(CfAppPluginExtension ext) {
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
