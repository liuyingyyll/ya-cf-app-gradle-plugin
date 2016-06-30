package io.pivotal.services.plugin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Responsible for cf push.
 */
public class CfPushTask extends DefaultTask {

	@TaskAction
	public void push() {
		CfPushPluginExtension extension = getProject().getExtensions().findByType(CfPushPluginExtension.class);
		CloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
				.host(extension.getHost())
				.username(extension.getUser())
				.password(extension.getPassword())
				.skipSslValidation(true)
				.build();


		CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
				.cloudFoundryClient(cfClient)
				.organization(extension.getOrg())
				.space(extension.getSpace())
				.build();


		File file = new File(extension.getPath());
		try {
			try (InputStream ios = new FileInputStream(file)) {
				Mono<Void> resp = cfOperations.applications()
						.push(PushApplicationRequest.builder()
								.name(extension.getName())
								.application(ios)
								.buildpack(extension.getBuildpack())
								.command(extension.getCommand())
								.diskQuota(extension.getDiskQuota())
								.instances(extension.getInstances())
								.memory(extension.getMemory())
								.timeout(extension.getHealthCheckTimeout())
								.noStart(true)
								.build());

				if (extension.getEnvironment() != null) {
					for (Map.Entry<String, String> entry : extension.getEnvironment().entrySet()) {
						resp = resp.then(cfOperations.applications().setEnvironmentVariable(SetEnvironmentVariableApplicationRequest
								.builder()
								.name(extension.getName())
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
										.applicationName(extension.getName())
										.build()));
					}
				}

				resp.then(cfOperations.applications().restart(RestartApplicationRequest
						.builder()
						.name(extension.getName()).build())).block(300_000L);

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
