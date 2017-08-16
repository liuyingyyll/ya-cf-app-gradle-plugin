package io.pivotal.services.plugin;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;

/**
 * Details of a service instance
 *
 * @author Biju Kunjummen
 */

@Value.Immutable(copy = true)
public abstract class CfServiceDetail {
    public abstract String name();

    public abstract String plan();

    public abstract String instanceName();

    @Nullable
    public abstract List<String> tags();
}
