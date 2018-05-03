package io.pivotal.services.plugin;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfPropertiesEnvOverrideTest {

    private Project project;

    private CfPluginExtension pluginExtension;

    private Map<String, Object> currentProjectProperties = new HashMap<>();

    @Before
    public void setUp() {
        this.project = mock(Project.class);
        ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
        this.pluginExtension = sampleExtension(this.project);
        when(extensionContainer.findByType(CfPluginExtension.class)).thenReturn(this.pluginExtension);
        when(this.project.getExtensions()).thenReturn(extensionContainer);
    }


    @Test
    public void testCcUserIsOverriddenViaEnvProperty() {
        Map<String, String> env = getEnvWithProperties("CF_CCUSER", "envuser");
        CfPropertiesMapper cfPropertiesMapper = new CfPropertiesMapper(this.project, env);

        CfProperties props = cfPropertiesMapper.getProperties();

        assertThat(props.ccUser()).isEqualTo("envuser");
    }


    @Test
    public void testCcPasswordIsOverriddenViaEnv() {
        Map<String, String> env = getEnvWithProperties("CF_CCPASSWORD", "envpasswd");
        CfPropertiesMapper cfPropertiesMapper = new CfPropertiesMapper(this.project, env);
        CfProperties props = cfPropertiesMapper.getProperties();

        assertThat(props.ccPassword()).isEqualTo("envpasswd");
    }


    private Map<String, String> getEnvWithProperties(String propertyName, String propertyValue) {
        Map<String, String> env = new HashMap<>();
        env.put(propertyName, propertyValue);
        return env;
    }


    private CfPluginExtension sampleExtension(Project project) {
        CfPluginExtension ext = new CfPluginExtension(project);
        ext.setName("name-fromplugin");
        ext.setCcUser("ccuser-fromplugin");
        ext.setCcHost("cchost-fromplugin");
        ext.setCcPassword("ccpassword-fromplugin");
        ext.setOrg("org-fromplugin");
        ext.setSpace("space-fromplugin");
        ext.setHost("route-fromplugin");
        ext.setHost("hostname-fromplugin");

        return ext;
    }
}
