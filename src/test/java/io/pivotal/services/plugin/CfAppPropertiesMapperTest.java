package io.pivotal.services.plugin;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfAppPropertiesMapperTest {

	private CfAppPropertiesMapper appPropertiesMapper ;

	@Before
	public void setUp() {
		Project project = mock(Project.class);
		ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
		when(extensionContainer.findByType(CfAppPluginExtension.class)).thenReturn(sampleExtension());
		when(project.getExtensions()).thenReturn(extensionContainer);
		appPropertiesMapper = Mockito.spy(new CfAppPropertiesMapper(project));
	}

	@Test
	public void testSimplePropertiesMapper() {
		CfAppProperties props = this.appPropertiesMapper.getProperties();
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

	@Test
	public void testGettingANewPropertyWithNameChanged() {
		CfAppProperties props = this.appPropertiesMapper.getProperties();
		CfAppProperties newProps = this.appPropertiesMapper.copyPropertiesWithNameChange(props, "mynewname");
		assertThat(newProps.getName()).isEqualTo("mynewname");
		assertThat(props.getName()).isEqualTo("name-fromplugin");
	}

	@Test
	public void testGettingANewPropertyWithNameAndRouteChanged() {
		CfAppProperties props = this.appPropertiesMapper.getProperties();
		CfAppProperties newProps = this.appPropertiesMapper.copyPropertiesWithNameAndRouteChange(props,
				"mynewname", "mynewroute");

		assertThat(props.getName()).isEqualTo("name-fromplugin");
		assertThat(newProps.getName()).isEqualTo("mynewname");
		assertThat(props.getHostName()).isEqualTo("route-fromplugin");
		assertThat(newProps.getHostName()).isEqualTo("mynewroute");
	}

	private CfAppPluginExtension sampleExtension() {
		CfAppPluginExtension ext = new CfAppPluginExtension();
		ext.setName("name-fromplugin");
		ext.setCcHost("cchost-fromplugin");
		ext.setCcPassword("ccpassword-fromplugin");
		ext.setBuildpack("buildpack-fromplugin");
		ext.setOrg("org-fromplugin");
		ext.setSpace("space-fromplugin");
		ext.setHostName("route-fromplugin");
		ext.setMemory(12);
		ext.setInstances(3);
		return ext;
	}


}
