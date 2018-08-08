package io.pivotal.services.plugin;

import java.io.File;
import java.util.stream.Collectors;

import org.cloudfoundry.operations.applications.ApplicationHealthCheck;
import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.cloudfoundry.operations.applications.ApplicationManifestUtils;
import org.cloudfoundry.operations.applications.Route;

public class CfManifestUtil {
    public static ApplicationManifest convert(CfProperties properties) {
        ApplicationManifest.Builder builder = ApplicationManifest.builder();
        if(properties.manifestPath() != null)
            builder.from(ApplicationManifestUtils.read(new File(properties.manifestPath()).toPath()).get(0));

        builder
            .buildpack(properties.buildpack())
            .command(properties.command())
            .disk(properties.diskQuota())
            .environmentVariables(properties.environment())
            .instances(properties.instances())
            .memory(properties.memory())
            .name(properties.name())
            .timeout(properties.timeout());
        if(properties.filePath() != null)
            builder.path(new File(properties.filePath()).toPath());
        if(properties.services() != null && !properties.services().isEmpty())
            builder.addAllServices(properties.services());
        if(properties.host() != null)
            builder.host(properties.host());
        if(properties.domain() != null)
            builder.domain(properties.domain());
        if(properties.path() != null)
            builder.routePath(properties.path());
        if(properties.healthCheckType() != null)
            builder.healthCheckType(ApplicationHealthCheck.from(properties.healthCheckType()));
        if(properties.routes() != null && !properties.routes().isEmpty())
           builder.routes(properties.routes().stream().map(s -> Route.builder().route(s).build()).collect(Collectors.toList()));
        return builder.build();
    }
}
