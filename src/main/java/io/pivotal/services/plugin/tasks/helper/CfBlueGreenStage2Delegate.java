package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.CfAppPropertiesMapper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;
import reactor.core.publisher.Mono;

import java.util.Optional;

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

	private CfMapRouteTaskDelegate mapRouteDelegate = new CfMapRouteTaskDelegate();
	private CfUnMapRouteTaskDelegate unMapRouteDelegate = new CfUnMapRouteTaskDelegate();
	private CfAppDetailsTaskDelegate detailsTaskDelegate = new CfAppDetailsTaskDelegate();
	private CfRenameAppTaskDelegate renameAppTaskDelegate = new CfRenameAppTaskDelegate();
	private CfDeleteAppTaskDelegate deleteAppTaskDelegate = new CfDeleteAppTaskDelegate();
	private CfAppStopDelegate stopDelegate = new CfAppStopDelegate();

	public Mono<Void> runStage2(Project project, CloudFoundryOperations cfOperations,
								CfAppProperties cfAppProperties) {

		CfAppPropertiesMapper cfAppPropertiesMapper = new CfAppPropertiesMapper(project);

		CfAppProperties greenName = cfAppPropertiesMapper
				.copyPropertiesWithNameChange(cfAppProperties, cfAppProperties.getName() + "-green");

		CfAppProperties greenNameAndRoute = cfAppPropertiesMapper
				.copyPropertiesWithNameAndRouteChange(cfAppProperties, cfAppProperties.getName() + "-green",
						cfAppProperties.getHostName() + "-green");

		CfAppProperties blueName = cfAppPropertiesMapper.copyPropertiesWithNameChange(cfAppProperties,
				cfAppProperties.getName() + "-blue");

		Mono<Optional<ApplicationDetail>> existingBlueAppDetailMono = detailsTaskDelegate
				.getAppDetails(cfOperations, blueName);

		Mono<Void> bgResult = existingBlueAppDetailMono.then(appDetail -> {
			//if a backup app is already present..
			if (appDetail.isPresent()) {
				return deleteAppTaskDelegate.deleteApp(cfOperations, blueName)
						.then(mapRouteDelegate.mapRoute(cfOperations, greenName))
						.then(unMapRouteDelegate.unmapRoute(cfOperations, cfAppProperties))
						.then(unMapRouteDelegate.unmapRoute(cfOperations, greenNameAndRoute))
						.then(renameAppTaskDelegate.renameApp(cfOperations, cfAppProperties, blueName))
						.then(renameAppTaskDelegate.renameApp(cfOperations, greenName, cfAppProperties))
						.then(stopDelegate.stopApp(cfOperations, blueName));
			} else {
				return mapRouteDelegate.mapRoute(cfOperations, greenName)
						.then(unMapRouteDelegate.unmapRoute(cfOperations, cfAppProperties))
						.then(unMapRouteDelegate.unmapRoute(cfOperations, greenNameAndRoute))
						.then(renameAppTaskDelegate.renameApp(cfOperations, cfAppProperties, blueName))
						.then(renameAppTaskDelegate.renameApp(cfOperations, greenName, cfAppProperties))
						.then(stopDelegate.stopApp(cfOperations, blueName));
			}
		});

		return bgResult;
	}
}
