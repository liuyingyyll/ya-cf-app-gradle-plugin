package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPluginExtension;
import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfPropertiesMapper;
import io.pivotal.services.plugin.CfProxySettingsDetail;
import io.pivotal.services.plugin.cf.StaticTokenProvider;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.ProxyConfiguration;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.Optional;

/**
 * Base class for all Concrete CF tasks
 */
abstract class AbstractCfTask extends DefaultTask {

    protected long defaultWaitTimeout = 600_000L; // 20 mins

    static Logger LOGGER = Logging.getLogger(AbstractCfTask.class);

    protected CfPropertiesMapper cfPropertiesMapper;

    protected AbstractCfTask() {
        this.cfPropertiesMapper = new CfPropertiesMapper(getProject());
    }

    protected CloudFoundryOperations getCfOperations() {
        CfProperties cfAppProperties = this.cfPropertiesMapper.getProperties();

        ConnectionContext connectionContext = DefaultConnectionContext.builder()
            .apiHost(cfAppProperties.ccHost())
            .skipSslValidation(true)
            .proxyConfiguration(tryGetProxyConfiguration(cfAppProperties))
            .build();

        TokenProvider tokenProvider = getTokenProvider(cfAppProperties);

        CloudFoundryClient cfClient = ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();

        CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cfClient)
            .organization(cfAppProperties.org())
            .space(cfAppProperties.space())
            .build();

        return cfOperations;
    }

    protected Optional<ProxyConfiguration> tryGetProxyConfiguration(CfProperties cfAppProperties) {
        final CfProxySettingsDetail proxySettings = cfAppProperties.cfProxySettings();
        if (proxySettings == null) {
            return Optional.empty();
        }
        if (proxySettings.proxyHost() == null || proxySettings.proxyPort() == null) {
            throw new IllegalStateException("At least host and port for proxy settings must be provided");
        }
        return Optional.of(ProxyConfiguration.builder()
            .host(proxySettings.proxyHost())
            .port(proxySettings.proxyPort())
            .username(Optional.ofNullable(proxySettings.proxyUser()))
            .password(Optional.ofNullable(proxySettings.proxyPassword()))
            .build());
    }

    protected TokenProvider getTokenProvider(CfProperties cfAppProperties) {
        if (cfAppProperties.ccToken() == null &&
            (cfAppProperties.ccUser() == null && cfAppProperties.ccPassword() == null)) {
            throw new IllegalStateException("One of token or user/password should be provided");
        }

        if (cfAppProperties.ccToken() != null) {
            return new StaticTokenProvider(cfAppProperties.ccToken());
        } else {
            return PasswordGrantTokenProvider.builder()
                .password(cfAppProperties.ccPassword())
                .username(cfAppProperties.ccUser())
                .build();
        }
    }

    protected CfPluginExtension getExtension() {
        return this.getProject().getExtensions().findByType(CfPluginExtension.class);
    }

    protected CfProperties getCfProperties() {
        return this.cfPropertiesMapper.getProperties();
    }

    @Override
    public String getGroup() {
        return "Cloud Foundry";
    }
}
