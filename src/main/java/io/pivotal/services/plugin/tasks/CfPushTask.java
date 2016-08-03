package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.helper.CfPushTaskDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for cf push.
 *
 * @author Biju Kunjummen
 */
public class CfPushTask extends AbstractCfTask {

	private CfPushTaskDelegate pushTaskDelegate = new CfPushTaskDelegate();

	@TaskAction
	public void push() {
		CfAppProperties cfAppProperties = getCfAppProperties();
		LOGGER.info("About to call Push task : {} ", cfAppProperties.toString());

		CloudFoundryOperations cfOperations = getCfOperations();

		Mono<Void> resp = pushTaskDelegate.push(cfOperations, cfAppProperties);
		resp.block(Duration.ofMillis(defaultWaitTimeout));

	}

	@Override
	public String getDescription() {
		return "Pushes an Application to Cloud Foundry";
	}
}
