package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPluginExtension;
import io.pivotal.services.plugin.CfProperties;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyOverridesTest {

	@Test
	public void testPropertyOverridesForAppName() {
		Project project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("cf-app");
		setPropsInExtension((CfPluginExtension)project.getExtensions().getByName("cfConfig"));

		CfPushTask cfPushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfProperties props = cfPushTask.getCfProperties();
		assertThat(props.name()).isEqualTo("name-fromplugin");
		assertThat(props.ccHost()).isEqualTo("cchost-fromplugin");
		assertThat(props.ccPassword()).isEqualTo("ccpassword-fromplugin");
		assertThat(props.buildpack()).isEqualTo("buildpack-fromplugin");
		assertThat(props.org()).isEqualTo("org-fromplugin");
		assertThat(props.space()).isEqualTo("space-fromplugin");
		assertThat(props.instances()).isEqualTo(3);
		assertThat(props.memory()).isEqualTo(12);
		assertThat(props.stagingTimeout()).isEqualTo(15);
		assertThat(props.startupTimeout()).isEqualTo(5);
	}


	private void setPropsInExtension(CfPluginExtension ext) {
		ext.setName("name-fromplugin");
		ext.setCcHost("cchost-fromplugin");
		ext.setCcUser("ccuser-fromplugin");
		ext.setCcPassword("ccpassword-fromplugin");
		ext.setBuildpack("buildpack-fromplugin");
		ext.setOrg("org-fromplugin");
		ext.setSpace("space-fromplugin");
		ext.setMemory(12);
		ext.setInstances(3);
	}

}
