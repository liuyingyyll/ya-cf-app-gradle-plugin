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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.cloudfoundry.operations.applications.ApplicationManifestUtils;
import org.cloudfoundry.operations.applications.Route;
import org.gradle.api.Project;

/**
 * Responsible for mapping plugin extension provided via a block
 * which looks like this:
 * <pre>
 *     cfConfig {
 *       ccHost = "api.local.pcfdev.io"
 *       ccUser = "admin"
 *       ccPassword = "admin"
 *       org = "pcfdev-org"
 *       space = "pcfdev-space"
 *       ....
 *       }
 * </pre>
 * OR overridden via command line flags say:
 * <pre>
 *    ./gradlew cf-push -Pcf.name=newname
 * </pre>
 * <p>
 * and providing a structure with the final values
 *
 * @author Biju Kunjummen
 */
public class CfPropertiesMapper {

    private final Project project;

    private final Map<String, String> systemEnv;

    private ApplicationManifest manifest;

    public CfPropertiesMapper(Project project) {
        this.project = project;
        this.systemEnv = System.getenv();
    }

    //For Tests
    CfPropertiesMapper(Project project, Map<String, String> systemEnv) {
        this.project = project;
        this.systemEnv = systemEnv;
    }

    public CfProperties getProperties() {
        if(getManifestPath() != null) {
            manifest = ApplicationManifestUtils.read(new File(getManifestPath()).toPath()).get(0);
        }
        return ImmutableCfProperties.builder()
            .name(getCfApplicationName())
            .ccHost(getCcHost())
            .ccUser(getCcUser())
            .ccPassword(getCcPassword())
            .ccToken(getCcToken())
            .org(getOrg())
            .space(getSpace())
            .manifestPath(getManifestPath())
            .filePath(getFilePath())
            .host(getAppHostName())
            .domain(getAppDomain())
            .path(getCfPath())
            .addAllRoutes(getAppRoutes())
            .state(this.getExtension().getState())
            .buildpack(this.getBuildpack())
            .command(this.getCommand())
            .console(this.getExtension().getConsole())
            .debug(this.getExtension().getDebug())
            .detectedStartCommand(this.getExtension().getDetectedStartCommand())
            .diskQuota(this.getDiskQuota())
            .enableSsh(this.getExtension().getEnableSsh())
            .environment(getEnvironment())
            .timeout(this.getTimeout())
            .healthCheckType(this.getHealthCheckType())
            .instances(this.getInstances())
            .memory(this.getMemory())
            .ports(this.getExtension().getPorts())
            .services(getServices())
            .stagingTimeout(this.getStagingTimeout())
            .startupTimeout(this.getStartupTimeout())
            .cfServices(this.getCfServices())
            .cfUserProvidedServices(this.getCfUserProvidedServices())
            .cfProxySettings(this.getCfProxySettings())
            .build();
    }

    private List<CfServiceDetail> getCfServices() {
        return mapCfServices(this.getExtension().getCfServices());
    }

    List<CfServiceDetail> mapCfServices(List<CfService> cfServices) {
        List<CfServiceDetail> serviceDetails = new ArrayList<>();
        if (cfServices != null) {
            for (CfService cfService : cfServices) {
                CfServiceDetail cfServiceDetail = ImmutableCfServiceDetail.builder()
                    .instanceName(cfService.getInstanceName())
                    .name(cfService.getName())
                    .plan(cfService.getPlan())
                    .tags(cfService.getTags())
                    .completionTimeout((cfService.getCompletionTimeout() != null)
                        ? cfService.getCompletionTimeout()
                        : DefaultProperties.SERVICE_CREATION_COMPLETION_TIMEOUT)
                    .build();

                serviceDetails.add(cfServiceDetail);
            }
        }
        return serviceDetails;
    }

    private List<CfUserProvidedServiceDetail> getCfUserProvidedServices() {
        return mapCfUserProvidedServices(this.getExtension().getCfUserProvidedServices());
    }

    List<CfUserProvidedServiceDetail> mapCfUserProvidedServices(List<CfUserProvidedService> cfUserProvidedServices) {
        List<CfUserProvidedServiceDetail> serviceDetails = new ArrayList<>();
        if (cfUserProvidedServices != null) {
            for (CfUserProvidedService service : cfUserProvidedServices) {
                CfUserProvidedServiceDetail cfServiceDetail = ImmutableCfUserProvidedServiceDetail.builder()
                    .instanceName(service.getInstanceName())
                    .credentials(service.getCredentials())
                    .completionTimeout((service.getCompletionTimeout() != null)
                        ? service.getCompletionTimeout()
                        : DefaultProperties.SERVICE_CREATION_COMPLETION_TIMEOUT)
                    .build();
                serviceDetails.add(cfServiceDetail);
            }
        }

        return serviceDetails;
    }

    private CfProxySettingsDetail getCfProxySettings() {
        final CfProxySettings proxySettings = this.getExtension().getCfProxySettings();
        if (proxySettings == null) {
            return null;
        }
        return ImmutableCfProxySettingsDetail.builder()
            .proxyHost(getStringPropertyFromProject(PropertyNameConstants.CF_PROXY_HOST)
                .orElse(proxySettings.getProxyHost()))
            .proxyPort(getIntegerPropertyFromProject(PropertyNameConstants.CF_PROXY_PORT)
                .orElse(proxySettings.getProxyPort()))
            .proxyUser(getStringPropertyFromProject(PropertyNameConstants.CF_PROXY_USER)
                .orElse(proxySettings.getProxyUser()))
            .proxyPassword(getStringPropertyFromProject(PropertyNameConstants.CF_PROXY_PASSWORD)
                .orElse(proxySettings.getProxyPassword()))
            .build();
    }

    CfPluginExtension getExtension() {
        return this.project.getExtensions().findByType(CfPluginExtension.class);
    }


    public String getCfApplicationName() {
        return firstNotNull(() -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NAME),
            () -> Optional.of(this.getExtension().getName()),
            () -> Optional.of(this.manifest.getName())
        );
    }

    public String getNewName() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NEW_NAME)
            .orElse(null);
    }

    public String getManifestPath() {
        return firstNotNull(
            () -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_MANIFEST),
            () -> Optional.of(this.getExtension().getManifestFile())
        );
    }

    public String getHealthCheckType() {
        return firstNotNull(
            () -> Optional.of(this.getExtension().getHealthCheckType()),
            () -> Optional.of(this.manifest.getHealthCheckType().getValue())
        );
    }

    public String getCommand() {
        return firstNotNull(
            () -> Optional.of(this.getExtension().getCommand()),
            () -> Optional.of(this.manifest.getCommand())
        );
    }

    public String getAppHostName() {
        return firstNotNull(() -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_HOST_NAME),
            () -> Optional.of(this.getExtension().getHost()),
            () -> Optional.of(this.manifest.getHosts().get(0))
        );
    }

    public String getAppDomain() {
        return firstNotNull(() -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_DOMAIN),
            () -> Optional.of(this.getExtension().getDomain()),
            () -> Optional.of(this.manifest.getDomains().get(0))
        );
    }

    public String getFilePath() {
        return firstNotNull(() -> getStringPropertyFromProject(PropertyNameConstants.CF_FILE_PATH),
            () -> Optional.of(this.getExtension().getFilePath()),
            () -> Optional.of(this.manifest.getPath().toString())
        );
    }

    public String getCcHost() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_HOST)
            .orElse(this.getExtension().getCcHost());
    }

    public String getCcUser() {
        return getPropertyFromEnvironment(PropertyNameConstants.CC_USER_ENV)
            .orElse(
                this.getStringPropertyFromProject(PropertyNameConstants.CC_USER)
                    .orElse(this.getExtension().getCcUser()));

    }

    public String getCcPassword() {
        return getPropertyFromEnvironment(PropertyNameConstants.CC_PASSWORD_ENV)
            .orElse(getStringPropertyFromProject(PropertyNameConstants.CC_PASSWORD)
                .orElse(this.getExtension().getCcPassword()));
    }

    public List<String> getServices() {
        return firstNotNull(() -> getListPropertyFromProject(PropertyNameConstants.CF_SERVICES),
            () -> Optional.of(this.getExtension().getServices()),
            () -> Optional.of(this.manifest.getServices())
        );
    }

    public String getCcToken() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_TOKEN)
            .orElse(this.getExtension().getCcToken());
    }

    public String getBuildpack() {
        return firstNotNull(() -> getStringPropertyFromProject(PropertyNameConstants.CF_BUILDPACK),
            () -> Optional.of(this.getExtension().getBuildpack()),
            () -> Optional.of(this.manifest.getBuildpack())
        );
    }

    public String getOrg() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_ORG)
            .orElse(this.getExtension().getOrg());
    }

    public String getSpace() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_SPACE)
            .orElse(this.getExtension().getSpace());
    }

    public String getCfPath() {
        return firstNotNull(() -> getStringPropertyFromProject(PropertyNameConstants.CF_PATH),
            () -> Optional.of(this.getExtension().getPath()),
            () -> Optional.of(this.manifest.getRoutePath())
        );
    }

    public List<String> getAppRoutes() {
        return firstNotNull(() -> getListPropertyFromProject(PropertyNameConstants.CF_APPLICATION_ROUTES),
            () -> Optional.of(this.getExtension().getRoutes()),
            () -> Optional.of(this.manifest.getRoutes().stream().map(Route::getRoute).collect(Collectors.toList()))
            );
    }

    public Integer getInstances() {
        return firstNotNull(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_INSTANCES),
            () -> Optional.of(this.getExtension().getInstances()),
            () -> Optional.of(this.manifest.getInstances())
        );
    }

    public Integer getMemory() {
        return firstNotNull(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_MEMORY),
            () -> Optional.of(this.getExtension().getMemory()),
            () -> Optional.of(this.manifest.getMemory())
        );
    }

    public Integer getTimeout() {
        return firstNotNull(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT),
            () -> Optional.of(this.getExtension().getTimeout()),
            () -> Optional.of(this.manifest.getTimeout())
        );
    }

    public Integer getDiskQuota() {
        return firstNotNull(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_DISK_QUOTA),
            () -> Optional.of(this.getExtension().getDiskQuota()),
            () -> Optional.of(this.manifest.getDisk())
        );
    }

    public Integer getStagingTimeout() {
        Integer stagingTimeout = getIntegerPropertyFromProject(PropertyNameConstants.CF_STAGING_TIMEOUT)
            .orElse(this.getExtension().getStagingTimeout());

        return stagingTimeout != null ? stagingTimeout : DefaultProperties.STAGING_TIMEOUT;
    }

    public Integer getStartupTimeout() {
        Integer startupTimeout = getIntegerPropertyFromProject(PropertyNameConstants.CF_STARTUP_TIMEOUT)
            .orElse(this.getExtension().getStartupTimeout());

        return startupTimeout != null ? startupTimeout : DefaultProperties.STARTUP_TIMEOUT;
    }

    private Map<String, String> getEnvironment() {
        Map<String, String> withProperties = new HashMap<>();
        Map<String, Object> manifestEnvironment = manifest.getEnvironmentVariables();
        if (manifestEnvironment != null)
            manifestEnvironment.forEach((key, obj) -> withProperties.put(key, obj.toString()));
        Map<String, String> buildScriptEnvironment = this.getExtension().getEnvironment();
        if (buildScriptEnvironment != null)
            withProperties.putAll(buildScriptEnvironment);

        Map<String, ?> allProperties = this.project.getProperties();
        for (String propName : allProperties.keySet()) {
            String prefix = PropertyNameConstants.CF_ENVIRONMENT + ".";
            if (propName.startsWith(prefix)) {
                String newKey = propName.substring(prefix.length());
                withProperties.put(newKey, getStringPropertyFromProject(propName).orElse(""));
            }
        }

        return withProperties;
    }

    public Optional<String> getPropertyFromEnvironment(String propertyName) {
        if (this.systemEnv.containsKey(propertyName)) {
            return Optional.of((String) this.systemEnv.get(propertyName));
        }
        return Optional.empty();
    }

    public Optional<String> getStringPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            return Optional.of((String) this.project.property(propertyName));
        }
        return Optional.empty();
    }

    public Optional<List<String>> getListPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            String rawProperty = (String) this.project.property(propertyName);
            return Optional.of((Arrays.asList(rawProperty.split(","))));
        }
        return Optional.empty();
    }

    /**
     * Get a property value from the Project properties.
     *
     * @param propertyName name of property
     * @return value of property, @{link java.util.Optional#empty} if not available.
     */
    public Optional<Integer> getIntegerPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            return Optional.of((Integer) this.project.property(propertyName));
        }
        return Optional.empty();
    }

    @SafeVarargs
    static <T> T firstNotNull(Supplier<Optional<T>>... optSup) {
        for(Supplier<Optional<T>> sup: optSup) {
            try {
                return sup.get().get();
            } catch(NoSuchElementException | NullPointerException ignored) {}
        }
        return null;
    }
}
