package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class CfBlueGreenStage1Delegate {

    private CfPushDelegate pushDelegate = new CfPushDelegate();
    private CfAppDetailsDelegate appDetailsDelegate = new CfAppDetailsDelegate();

    private static final Logger LOGGER = Logging
        .getLogger(CfBlueGreenStage1Delegate.class);

    public Mono<Void> runStage1(Project project, CloudFoundryOperations cfOperations,
                                CfProperties cfProperties) {

        Mono<Optional<ApplicationDetail>> appDetailMono = appDetailsDelegate
            .getAppDetails(cfOperations, cfProperties);

        Mono<CfProperties> cfPropertiesMono = appDetailMono.map((appDetailOpt) -> {

            LOGGER.lifecycle(
                "Running Blue Green Deploy - deploying a 'green' app. App '{}' with route '{}'",
                cfProperties.name(), cfProperties.host());

            return appDetailOpt.map(appDetail -> {
                printAppDetail(appDetail);
                return ImmutableCfProperties.copyOf(cfProperties)
                    .withName(cfProperties.name() + "-green")
                    .withHost(cfProperties.host() + "-green")
                    .withInstances(appDetail.getInstances())
                    .withMemory(appDetail.getMemoryLimit())
                    .withDiskQuota(appDetail.getDiskQuota());
            }).orElse(ImmutableCfProperties.copyOf(cfProperties)
                .withName(cfProperties.name() + "-green")
                .withHost(cfProperties.host() + "-green"));
        });

        return cfPropertiesMono.flatMap(
            withNewNameAndRoute -> pushDelegate.push(cfOperations, withNewNameAndRoute));
    }

    private void printAppDetail(ApplicationDetail applicationDetail) {
        LOGGER.lifecycle("Application Name: {}", applicationDetail.getName());
        LOGGER.lifecycle("Intance Count: {}", applicationDetail.getInstances());
        LOGGER
            .lifecycle("Running Instances: {}", applicationDetail.getRunningInstances());
    }
}
