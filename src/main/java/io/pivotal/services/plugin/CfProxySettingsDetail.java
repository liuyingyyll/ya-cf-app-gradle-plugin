package io.pivotal.services.plugin;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * Details of the proxy settings
 *
 * @author Andreas Schilling
 */
@Value.Immutable(copy = true)
public abstract class CfProxySettingsDetail {
    public abstract String proxyHost();

    public abstract Integer proxyPort();

    @Nullable
    public abstract String proxyUser();

    @Nullable
    public abstract String proxyPassword();
}
