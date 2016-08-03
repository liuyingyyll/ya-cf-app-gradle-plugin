package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppPluginExtension;
import io.pivotal.services.plugin.CfAppProperties;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyEnvironmentOverridesTest {

	@Test
	public void testPropertyOverridesForAppName() {
		Project project = ProjectBuilder.builder().build();
		project.getPluginManager().apply("cf-app");
		setPropsInExtension((CfAppPluginExtension)project.getExtensions().getByName("cfConfig"));
		overrideProjectProperties(project);

		CfPushTask cfPushTask = (CfPushTask)project.getTasks().getAt("cf-push");
		CfAppProperties props = cfPushTask.getCfAppProperties();
		assertThat(props.getName()).isEqualTo("appName-new");
		assertThat(props.getCcHost()).isEqualTo("cchost-new");
		assertThat(props.getCcPassword()).isEqualTo("ccpassword-new");
		assertThat(props.getOrg()).isEqualTo("org-new");
		assertThat(props.getSpace()).isEqualTo("space-new");
		assertThat(props.getDomain()).isEqualTo("domain-new");
	}

	private void setPropsInExtension(CfAppPluginExtension ext) {
		ext.setName("appName");
		ext.setCcHost("cchost");
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
