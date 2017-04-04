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

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * route -&gt; app
 * <p>
 * Stage 1:
 * Step 1:
 * route-green -&gt; app-green
 * ...testing after stage 1..
 * Stage 2:
 * Step 2:
 * route       -&gt; app-green, app
 * route-green -&gt; app-green
 * <p>
 * Step 3:
 * route       -&gt; app-green
 * route-green -&gt; app-green
 * -&gt; app
 * Step 4:
 * route       -&gt; app-green
 * -&gt; app
 * <p>
 * Step 5:
 * route       -&gt; app-green
 * -&gt; app-blue
 * <p>
 * Step 6:
 * route       -&gt; app
 * -&gt; app-blue
 */
public class CfBlueGreenStage2Delegate {

    private static final Logger LOGGER = Logging
        .getLogger(CfBlueGreenStage2Delegate.class);

    private CfMapRouteDelegate mapRouteDelegate = new CfMapRouteDelegate();
    private CfUnMapRouteDelegate unMapRouteDelegate = new CfUnMapRouteDelegate();
    private CfAppDetailsDelegate appDetailsDelegate = new CfAppDetailsDelegate();
    private CfRenameAppDelegate renameAppDelegate = new CfRenameAppDelegate();
    private CfDeleteAppDelegate deleteAppDelegate = new CfDeleteAppDelegate();
    private CfAppStopDelegate appStopDelegate = new CfAppStopDelegate();

    public Mono<Void> runStage2(Project project, CloudFoundryOperations cfOperations,
                                CfProperties cfProperties) {

        CfProperties greenName = ImmutableCfProperties.copyOf(cfProperties)
            .withName(cfProperties.name() + "-green");

        CfProperties greenNameAndRoute = ImmutableCfProperties.copyOf(cfProperties)
            .withName(cfProperties.name() + "-green")
            .withHostName(cfProperties.hostName() + "-green");

        CfProperties blueName = ImmutableCfProperties.copyOf(cfProperties)
            .withName(cfProperties.name() + "-blue");

        Mono<Optional<ApplicationDetail>> backupAppMono = appDetailsDelegate
            .getAppDetails(cfOperations, blueName);

        Mono<Optional<ApplicationDetail>> existingAppMono = appDetailsDelegate
            .getAppDetails(cfOperations, cfProperties);

        Mono<Void> bgResult = Mono.when(backupAppMono, existingAppMono)
            .then(function((backupApp, existingApp) -> {
                LOGGER.lifecycle(
                    "Running Blue Green Deploy - after deploying a 'green' app. App '{}' with route '{}'",
                    cfProperties.name(), cfProperties.hostName());

                return (backupApp.isPresent() ?
                    deleteAppDelegate.deleteApp(cfOperations, blueName) :
                    Mono.empty()).then(mapRouteDelegate.mapRoute(cfOperations, greenName))
                    .then(existingApp.isPresent() ?
                        unMapRouteDelegate.unmapRoute(cfOperations, cfProperties) :
                        Mono.empty())
                    .then(unMapRouteDelegate.unmapRoute(cfOperations, greenNameAndRoute))
                    .then(existingApp.isPresent() ?
                        renameAppDelegate
                            .renameApp(cfOperations, cfProperties, blueName) :
                        Mono.empty()).then(renameAppDelegate
                        .renameApp(cfOperations, greenName, cfProperties)).then(
                        existingApp.isPresent() ?
                            appStopDelegate.stopApp(cfOperations, blueName) :
                            Mono.empty());
            }));

        return bgResult;
    }
}
