package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
import io.pivotal.services.plugin.tasks.AbstractCfTask;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
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
 * Responsible for cf push.
 *
 * @author Biju Kunjummen
 */
public class CfPushTask extends AbstractCfTask {

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


		File file = new File(extension.getFilePath());
		try {
			try (InputStream ios = new FileInputStream(file)) {
				Mono<Void> resp = cfOperations.applications()
						.push(PushApplicationRequest.builder()
								.name(getCfApplicationName())
								.application(ios)
								.buildpack(extension.getBuildpack())
								.command(extension.getCommand())
								.diskQuota(extension.getDiskQuota())
								.instances(extension.getInstances())
								.memory(extension.getMemory())
								.timeout(extension.getHealthCheckTimeout())
								.domain(extension.getDomain())
								.host(getAppHostName())
								.noStart(true)
								.build());

				if (extension.getEnvironment() != null) {
					for (Map.Entry<String, String> entry : extension.getEnvironment().entrySet()) {
						resp = resp.then(cfOperations.applications().setEnvironmentVariable(SetEnvironmentVariableApplicationRequest
								.builder()
								.name(getCfApplicationName())
								.variableName(entry.getKey())
								.variableValue(entry.getValue())
								.build()));
					}
				}

				if (extension.getServices() != null) {
					for (String serviceName : extension.getServices()) {
						resp = resp.then(cfOperations.services()
								.bind(BindServiceInstanceRequest.builder()
										.serviceInstanceName(serviceName)
										.applicationName(getCfApplicationName())
										.build()));
					}
				}

				resp.then(cfOperations.applications().restart(RestartApplicationRequest
						.builder()
						.name(getCfApplicationName()).build())).block(600_000L);

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
