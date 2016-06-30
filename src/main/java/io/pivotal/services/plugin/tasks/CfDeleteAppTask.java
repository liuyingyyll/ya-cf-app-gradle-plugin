package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.DeleteApplicationRequest;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Responsible for Deleting an app
 *
 * @author Biju Kunjummen
 */
public class CfDeleteAppTask extends AbstractCfTask {

	@TaskAction
	public void push() {
		CfPushPluginExtension extension = getExtension();

		CloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
				.host(extension.getCcHost())
				.username(extension.getCcUser())
				.password(extension.getCcPassword())
				.skipSslValidation(true)
				.build();


		CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(cfClient)
				.organization(extension.getOrg())
				.space(extension.getSpace())
				.build();

		Mono<Void> resp = cfOperations.applications().delete(
				DeleteApplicationRequest
						.builder()
						.name(getCfApplicationName())
						.build());

		resp.block(600_000L);
	}


}
