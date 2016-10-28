package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.ImmutableCfProperties;
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
		CfProperties cfAppProperties = getCfProperties();

		if (getNewName() == null) {
			throw new RuntimeException("New name not provided");
		}

		CfProperties oldCfProperties = cfAppProperties;

		CfProperties newCfProperties = ImmutableCfProperties.copyOf(oldCfProperties).withName(getNewName());

		Mono<Void> resp = renameDelegate.renameApp(cfOperations, oldCfProperties, newCfProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}

	private String getNewName() {
		return this.cfPropertiesMapper.getNewName();
	}


	@Override
	public String getDescription() {
		return "Rename an Application";
	}
}
