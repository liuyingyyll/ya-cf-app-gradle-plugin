/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.services.plugin;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfPropertiesMapperTest {

    private CfPropertiesMapper cfPropertiesMapper;

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
        this.cfPropertiesMapper = new CfPropertiesMapper(this.project);
    }

    @Test
    public void testThatPropertiesAreRetrievedWhenSetViaExtensionContainer() {
        CfProperties props = this.cfPropertiesMapper.getProperties();
        assertThat(props.name()).isEqualTo("name-fromplugin");
        assertThat(props.ccHost()).isEqualTo("cchost-fromplugin");
        assertThat(props.ccPassword()).isEqualTo("ccpassword-fromplugin");
        assertThat(props.buildpack()).isEqualTo("buildpack-fromplugin");
        assertThat(props.org()).isEqualTo("org-fromplugin");
        assertThat(props.space()).isEqualTo("space-fromplugin");
        assertThat(props.ccUser()).isEqualTo("ccuser-fromplugin");
        assertThat(props.filePath()).isEqualTo("filepath-fromplugin");
        assertThat(props.host()).isEqualTo("hostname-fromplugin");
        assertThat(props.domain()).isEqualTo("domain-fromplugin");
        assertThat(props.path()).isEqualTo("path-fromplugin");
        assertThat(props.state()).isEqualTo("state-fromplugin");
        assertThat(props.command()).isEqualTo("command-fromplugin");
        assertThat(props.console()).isFalse();
        assertThat(props.detectedStartCommand()).isEqualTo("detectedcommand-fromplugin");

        assertThat(true).isTrue();

        assertThat(props.diskQuota()).isEqualTo(1000);
        assertThat(props.enableSsh()).isTrue();
        assertThat(props.environment()).containsKeys("env1", "env2").containsValues("env1value", "env2value");

        assertThat(props.timeout()).isEqualTo(150);
        assertThat(props.healthCheckType()).isEqualTo("healthchecktype-fromplugin");
        assertThat(props.instances()).isEqualTo(5);
        assertThat(props.memory()).isEqualTo(2000);
        assertThat(props.ports()).contains(8080, 8081);

        assertThat(props.services()).contains("service1", "service2");
        assertThat(props.stagingTimeout()).isEqualTo(101);
        assertThat(props.startupTimeout()).isEqualTo(102);

        assertThat(props.cfServices()).hasSize(1);
        assertThat(props.cfUserProvidedServices()).hasSize(1);

        assertThat(props.cfProxySettings()).isNotNull();
        assertThat(props.cfProxySettings().proxyHost()).isEqualTo("http://proxy.host");
        assertThat(props.cfProxySettings().proxyPort()).isEqualTo(1234);
        assertThat(props.cfProxySettings().proxyUser()).isEqualTo("proxy-user");
        assertThat(props.cfProxySettings().proxyPassword()).isEqualTo("proxy-password");
    }

    @Test
    public void testThatNameIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_APPLICATION_NAME, "newname");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.name()).isEqualTo("newname");
    }

    @Test
    public void testHostNameIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_APPLICATION_HOST_NAME, "newhost");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.host()).isEqualTo("newhost");
    }

    @Test
    public void testAppDomainIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_APPLICATION_DOMAIN, "newdomain");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.domain()).isEqualTo("newdomain");
    }

    @Test
    public void testFilePathIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_FILE_PATH, "newfilepath");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.filePath()).isEqualTo("newfilepath");
    }

    @Test
    public void testCloudControllerHostIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CC_HOST, "newcchost");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccHost()).isEqualTo("newcchost");
    }

    @Test
    public void testCcUserIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CC_USER, "newuser");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccUser()).isEqualTo("newuser");
    }

    @Test
    public void testTokenIsOverridden() {
        setProjectProperty(PropertyNameConstants.CC_TOKEN, "newtoken");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccToken()).isEqualTo("newtoken");
    }

    @Test
    public void testCcPasswordIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CC_PASSWORD, "newpwd");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccPassword()).isEqualTo("newpwd");
    }

    @Test
    public void testBuildpackIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_BUILDPACK, "newbuildpack");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.buildpack()).isEqualTo("newbuildpack");
    }

    @Test
    public void testOrgIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_ORG, "neworg");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.org()).isEqualTo("neworg");
    }

    @Test
    public void testSpaceIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_SPACE, "newspace");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.space()).isEqualTo("newspace");
    }

    @Test
    public void testPathIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_PATH, "newpath");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.path()).isEqualTo("newpath");
    }

    @Test
    public void testInstanceCountIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_INSTANCES, 10);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.instances()).isEqualTo(10);
    }

    @Test(expected = Exception.class)
    public void testNonNumericInstanceCountShouldThrowAnException() {
        setProjectProperty(PropertyNameConstants.CF_INSTANCES, "invalid");
        this.cfPropertiesMapper.getProperties();
    }

    @Test
    public void testMemoryIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_MEMORY, 100);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.memory()).isEqualTo(100);
    }

    @Test
    public void testTimeoutIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT, 10);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.timeout()).isEqualTo(10);
    }

    @Test
    public void testDiskQuotaIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_DISK_QUOTA, 101);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.diskQuota()).isEqualTo(101);
    }

    @Test
    public void testStagingTimeoutIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_STAGING_TIMEOUT, 6);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.stagingTimeout()).isEqualTo(6);
    }

    @Test
    public void testDefaultsForStagingTimeoutIs15Minutes() {
        pluginExtension.setStagingTimeout(null);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.stagingTimeout()).isEqualTo(15);
    }

    @Test
    public void testDefaultsForStartupTimeoutIs5Minutes() {
        pluginExtension.setStartupTimeout(null);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.startupTimeout()).isEqualTo(5);
    }

    @Test
    public void testStartupTimeoutIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_STARTUP_TIMEOUT, 7);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.startupTimeout()).isEqualTo(7);
    }

    @Test
    public void testCfServiceDetailMapper() {
        List<CfService> cfServices = new ArrayList<>();
        CfService s1 = new CfService();
        s1.setInstanceName("inst1");
        s1.setName("svcname1");
        s1.setPlan("svcplan");
        s1.setCompletionTimeout(15);
        cfServices.add(s1);

        List<CfServiceDetail> cfServicesMapped = this.cfPropertiesMapper.mapCfServices(cfServices);
        assertThat(cfServices).hasSize(1);

        assertThat(cfServicesMapped)
            .contains(ImmutableCfServiceDetail
                .builder()
                .instanceName("inst1")
                .name("svcname1")
                .plan("svcplan")
                .tags(null)
                .completionTimeout(15)
                .build());
    }

    @Test
    public void testCfServiceDetailDefaultConnectionTimeoutMapper() {
        List<CfService> cfServices = new ArrayList<>();
        CfService s1 = new CfService();
        s1.setInstanceName("inst1");
        s1.setName("svcname1");
        s1.setPlan("svcplan");
        cfServices.add(s1);

        List<CfServiceDetail> cfServicesMapped = this.cfPropertiesMapper.mapCfServices(cfServices);
        assertThat(cfServices).hasSize(1);

        assertThat(cfServicesMapped)
            .contains(ImmutableCfServiceDetail
                .builder()
                .instanceName("inst1")
                .name("svcname1")
                .plan("svcplan")
                .tags(null)
                .completionTimeout(10)
                .build());
    }

    @Test
    public void testCfUspDetailMapper() {
        List<CfUserProvidedService> cfUserServices = new ArrayList<>();
        CfUserProvidedService us1 = new CfUserProvidedService();
        us1.setInstanceName("user-inst1");
        us1.setCredentials(Collections.emptyMap());
        us1.setCompletionTimeout(21);
        cfUserServices.add(us1);

        List<CfUserProvidedServiceDetail> mapped = this.cfPropertiesMapper.mapCfUserProvidedServices(cfUserServices);
        assertThat(mapped).hasSize(1);
        assertThat(mapped)
            .contains(ImmutableCfUserProvidedServiceDetail
                .builder()
                .instanceName("user-inst1")
                .completionTimeout(21)
                .credentials(Collections.emptyMap()).build());
    }

    @Test
    public void testCfUspDetailMapperWithDefaultTimeout() {
        List<CfUserProvidedService> cfUserServices = new ArrayList<>();
        CfUserProvidedService us1 = new CfUserProvidedService();
        us1.setInstanceName("user-inst1");
        us1.setCredentials(Collections.emptyMap());
        cfUserServices.add(us1);

        List<CfUserProvidedServiceDetail> mapped = this.cfPropertiesMapper.mapCfUserProvidedServices(cfUserServices);
        assertThat(mapped).hasSize(1);
        assertThat(mapped)
            .contains(ImmutableCfUserProvidedServiceDetail
                .builder()
                .instanceName("user-inst1")
                .completionTimeout(10)
                .credentials(Collections.emptyMap()).build());
    }

    @Test
    public void testAddEnvironmentValues() {
        setProjectProperty("cf.environment.JAVA_OPTS", "newOpts");
        CfProperties properties = this.cfPropertiesMapper.getProperties();
        assertThat(properties.environment().get("JAVA_OPTS")).isEqualTo("newOpts");
    }

    @Test
    public void testOverrideEnvironmentValues() {
        setProjectProperty("cf.environment.env1", "env1NewValue");
        CfProperties properties = this.cfPropertiesMapper.getProperties();
        assertThat(properties.environment().get("env1")).isEqualTo("env1NewValue");
    }

    @Test
    public void testProxyHostIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_PROXY_HOST, "http://new-proxy.host");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.cfProxySettings().proxyHost()).isEqualTo("http://new-proxy.host");
    }

    @Test
    public void testProxyPortIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_PROXY_PORT, 4321);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.cfProxySettings().proxyPort()).isEqualTo(4321);
    }

    @Test
    public void testProxyUserIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_PROXY_USER, "new-proxy-user");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.cfProxySettings().proxyUser()).isEqualTo("new-proxy-user");
    }

    @Test
    public void testProxyPasswordIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_PROXY_PASSWORD, "new-proxy-password");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.cfProxySettings().proxyPassword()).isEqualTo("new-proxy-password");
    }


    private void setProjectProperty(String propertyName, Object propertyValue) {
        currentProjectProperties.put(propertyName, propertyValue);
        when(this.project.property(propertyName)).thenReturn(propertyValue);
        when(this.project.hasProperty(propertyName)).thenReturn(true);
        when(this.project.getProperties()).thenReturn((Map) currentProjectProperties);
    }


    private CfPluginExtension sampleExtension(Project project) {
        CfPluginExtension ext = new CfPluginExtension(project);
        ext.setName("name-fromplugin");
        ext.setCcUser("ccuser-fromplugin");
        ext.setCcHost("cchost-fromplugin");
        ext.setCcPassword("ccpassword-fromplugin");
        ext.setBuildpack("buildpack-fromplugin");
        ext.setOrg("org-fromplugin");
        ext.setSpace("space-fromplugin");
        ext.setHost("route-fromplugin");
        ext.setFilePath("filepath-fromplugin");
        ext.setHost("hostname-fromplugin");
        ext.setDomain("domain-fromplugin");
        ext.setPath("path-fromplugin");
        ext.setState("state-fromplugin");
        ext.setCommand("command-fromplugin");
        ext.setDetectedStartCommand("detectedcommand-fromplugin");
        ext.setEnableSsh(Boolean.TRUE);
        ext.setDiskQuota(1000);
        ext.setConsole(false);

        Map<String, String> envs = new HashMap<>();
        envs.put("env1", "env1value");
        envs.put("env2", "env2value");
        ext.setEnvironment(envs);

        ext.setTimeout(150);
        ext.setHealthCheckType("healthchecktype-fromplugin");
        ext.setInstances(5);

        ext.setMemory(2000);
        ext.setPorts(Arrays.asList(8080, 8081));

        ext.setServices(Arrays.asList("service1", "service2"));

        ext.setStagingTimeout(101);
        ext.setStartupTimeout(102);
        ext.setCcToken("cctoken");


        List<CfService> cfServices = new ArrayList<>();
        CfService s1 = new CfService();
        s1.setInstanceName("inst1");
        s1.setName("svcname1");
        s1.setPlan("svcplan");
        s1.setCompletionTimeout(15);
        cfServices.add(s1);
        ext.setCfServices(cfServices);


        List<CfUserProvidedService> cfUserServices = new ArrayList<>();
        CfUserProvidedService us1 = new CfUserProvidedService();
        us1.setInstanceName("user-inst1");
        us1.setCredentials(new HashMap<>());
        us1.setCompletionTimeout(21);
        cfUserServices.add(us1);
        ext.setCfUserProvidedServices(cfUserServices);

        CfProxySettings cfProxySettings = new CfProxySettings();
        cfProxySettings.setProxyHost("http://proxy.host");
        cfProxySettings.setProxyPort(1234);
        cfProxySettings.setProxyUser("proxy-user");
        cfProxySettings.setProxyPassword("proxy-password");
        ext.setCfProxySettings(cfProxySettings);

        return ext;
    }
}
