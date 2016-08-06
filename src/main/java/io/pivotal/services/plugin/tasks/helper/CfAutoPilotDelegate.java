package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.CfAppPropertiesMapper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;
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

	public Mono<Void> runAutopilot(Project project, CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {
		CfAppPropertiesMapper cfAppPropertiesMapper = new CfAppPropertiesMapper(project);
		CfAppProperties withNameChanged = cfAppPropertiesMapper
				.copyPropertiesWithNameChange(cfAppProperties, cfAppProperties.getName() + "-venerable");

		Mono<Optional<ApplicationDetail>> appDetailMono = detailsDelegate
				.getAppDetails(cfOperations, cfAppProperties);

		Mono<Void> autopilotResult = appDetailMono.then(appDetail -> {
			if (appDetail.isPresent()) {
				Mono<Void> renameResult = renameAppDelegate.renameApp(cfOperations, cfAppProperties, withNameChanged);
				return renameResult.then(pushDelegate.push(cfOperations, cfAppProperties))
						.then(deleteDelegate.deleteApp(cfOperations, withNameChanged));
			} else {
				return pushDelegate.push(cfOperations, cfAppProperties);
			}
		});

		return autopilotResult;
	}

}
