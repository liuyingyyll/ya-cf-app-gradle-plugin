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

/**
 * Responsible for handling Autopilot flow.
 */
public class CfAutoPilotDelegate {
    private CfPushDelegate pushDelegate = new CfPushDelegate();
    private CfRenameAppDelegate renameAppDelegate = new CfRenameAppDelegate();
    private CfDeleteAppDelegate deleteDelegate = new CfDeleteAppDelegate();
    private CfAppDetailsDelegate detailsDelegate = new CfAppDetailsDelegate();

    private static final Logger LOGGER = Logging.getLogger(CfAutoPilotDelegate.class);

    public Mono<Void> runAutopilot(Project project, CloudFoundryOperations cfOperations,
                                   CfProperties cfProperties) {
        LOGGER.lifecycle("Running Autopilot on App: {}", cfProperties.name());
        CfProperties withNameChanged = ImmutableCfProperties.copyOf(cfProperties)
            .withName(cfProperties.name() + "-venerable");

        Mono<Optional<ApplicationDetail>> appDetailMono = detailsDelegate
            .getAppDetails(cfOperations, cfProperties);

        Mono<Void> autopilotResult = appDetailMono.then(appDetailOpt -> {
            if (appDetailOpt.isPresent()) {
                ApplicationDetail appDetail = appDetailOpt.get();
                CfProperties withExistingDetails = ImmutableCfProperties
                    .copyOf(cfProperties).withInstances(appDetail.getInstances())
                    .withMemory(appDetail.getMemoryLimit())
                    .withDiskQuota(appDetail.getDiskQuota());
                Mono<Void> renameResult = renameAppDelegate
                    .renameApp(cfOperations, cfProperties, withNameChanged);
                return renameResult
                    .then(pushDelegate.push(cfOperations, withExistingDetails))
                    .then(deleteDelegate.deleteApp(cfOperations, withNameChanged));
            } else {
                return pushDelegate.push(cfOperations, cfProperties);
            }
        });

        return autopilotResult;
    }

}
