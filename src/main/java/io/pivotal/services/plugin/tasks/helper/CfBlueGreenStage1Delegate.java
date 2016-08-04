package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.CfAppPropertiesMapper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.Project;
import reactor.core.publisher.Mono;

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
 *             -&gt; app
 * Step 4:
 * route       -&gt; app-green
 *             -&gt; app
 * <p>
 * Step 5:
 * route       -&gt; app-green
 *             -&gt; app-blue
 * <p>
 * Step 6:
 * route       -&gt; app
 *             -&gt; app-blue
 */
public class CfBlueGreenStage1Delegate {

	private CfPushDelegate cfPushDelegate = new CfPushDelegate();

	public Mono<Void> runStage1(Project project, CloudFoundryOperations cfOperations,
								CfAppProperties cfAppProperties) {

		CfAppPropertiesMapper cfAppPropertiesMapper = new CfAppPropertiesMapper(project);
		CfAppProperties withNewNameAndRoute = cfAppPropertiesMapper
				.copyPropertiesWithNameAndRouteChange(cfAppProperties,
						cfAppProperties.getName() + "-green", cfAppProperties.getHostName() + "-green");

		return cfPushDelegate.push(cfOperations, withNewNameAndRoute);
	}
}
