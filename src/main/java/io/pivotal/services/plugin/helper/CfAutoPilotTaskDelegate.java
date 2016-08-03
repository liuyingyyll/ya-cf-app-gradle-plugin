package io.pivotal.services.plugin.helper;

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
public class CfAutoPilotTaskDelegate {
	private CfPushTaskDelegate cfPushTaskDelegate = new CfPushTaskDelegate();
	private CfRenameAppTaskDelegate cfRenameAppTaskDelegate = new CfRenameAppTaskDelegate();
	private CfDeleteAppTaskDelegate deleteDelegate = new CfDeleteAppTaskDelegate();
	private CfAppDetailsTaskDelegate detailsTaskDelegate = new CfAppDetailsTaskDelegate();

	public Mono<Void> runAutopilot(Project project, CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {
		CfAppPropertiesMapper cfAppPropertiesMapper = new CfAppPropertiesMapper(project);
		CfAppProperties withNameChanged = cfAppPropertiesMapper
				.copyPropertiesWithNameChange(cfAppProperties, cfAppProperties.getName() + "-venerable");

		Mono<Optional<ApplicationDetail>> appDetailMono = detailsTaskDelegate
				.getAppDetails(cfOperations, cfAppProperties);

		Mono<Void> autopilotResult = appDetailMono.then(appDetail -> {
			if (appDetail.isPresent()) {
				Mono<Void> renameResult = cfRenameAppTaskDelegate.renameApp(cfOperations, cfAppProperties, withNameChanged);
				return renameResult.then(cfPushTaskDelegate.push(cfOperations, cfAppProperties))
						.then(deleteDelegate.deleteApp(cfOperations, withNameChanged));
			} else {
				return cfPushTaskDelegate.push(cfOperations, cfAppProperties);
			}
		});

		return autopilotResult;
	}

}
