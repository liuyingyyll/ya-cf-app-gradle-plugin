package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPluginExtension;
import io.pivotal.services.plugin.CfProperties;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyEnvironmentOverridesTest {

	@Test
	public void testPropertyOverridesForAppName() {
		Project project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("cf-app");
		setPropsInExtension((CfPluginExtension)project.getExtensions().getByName("cfConfig"));
		overrideProjectProperties(project);

		CfPushTask cfPushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfProperties props = cfPushTask.getCfProperties();
		assertThat(props.name()).isEqualTo("appName-new");
		assertThat(props.ccHost()).isEqualTo("cchost-new");
		assertThat(props.ccPassword()).isEqualTo("ccpassword-new");
		assertThat(props.org()).isEqualTo("org-new");
		assertThat(props.space()).isEqualTo("space-new");
		assertThat(props.domain()).isEqualTo("domain-new");
	}

	private void setPropsInExtension(CfPluginExtension ext) {
		ext.setName("appName");
		ext.setCcHost("cchost");
		ext.setCcUser("ccuser");
		ext.setCcPassword("ccpassword");
		ext.setBuildpack("buildpack");
		ext.setOrg("org");
		ext.setSpace("space");
		ext.setDiskQuota(1);
		ext.setDomain("domain");
		ext.setMemory(12);
		ext.setInstances(3);
	}

	private void overrideProjectProperties(Project project) {
		project.getExtensions().getExtraProperties().set("cf.name", "appName-new");
		project.getExtensions().getExtraProperties().set("cf.ccHost", "cchost-new");
		project.getExtensions().getExtraProperties().set("cf.ccPassword", "ccpassword-new");
		project.getExtensions().getExtraProperties().set("cf.org", "org-new");
		project.getExtensions().getExtraProperties().set("cf.space", "space-new");
		project.getExtensions().getExtraProperties().set("cf.domain", "domain-new");
	}

}
