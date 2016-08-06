package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.tasks.helper.CfRenameAppDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for handling renaming an app.
 *
 * @author Biju Kunjummen
 */
public class CfRenameAppTask extends AbstractCfTask {

	private CfRenameAppDelegate renameDelegate = new CfRenameAppDelegate();

	@TaskAction
	public void renameApp() {
		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		if (getNewName() == null) {
			throw new RuntimeException("New name not provided");
		}

		CfAppProperties oldAppProperties = cfAppProperties;

		CfAppProperties newAppProperties =
				this.appPropertiesMapper.copyPropertiesWithNameChange(oldAppProperties, getNewName());

		Mono<Void> resp = renameDelegate.renameApp(cfOperations, oldAppProperties, newAppProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	private String getNewName() {
		return this.appPropertiesMapper.getNewName();
	}


	@Override
	public String getDescription() {
		return "Rename an Application";
	}
}
