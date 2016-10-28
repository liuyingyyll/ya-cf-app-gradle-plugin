package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Responsible for cf-push, since this functionality is going to be called from a few places this logic has been
 * centralized here.
 *
 * @author Biju Kunjummen
 */
public class CfPushDelegate {

	private static final Logger LOGGER = Logging.getLogger(CfPushDelegate.class);

	public Mono<Void> push(CloudFoundryOperations cfOperations, CfProperties cfProperties) {
		LOGGER.lifecycle("Pushing app '{}'", cfProperties.name());
		Path path = Paths.get(cfProperties.filePath());


		Mono<Void> resp = cfOperations.applications()
				.push(PushApplicationRequest.builder()
						.name(cfProperties.name())
						.application(path)
						.buildpack(cfProperties.buildpack())
						.command(cfProperties.command())
						.diskQuota(cfProperties.diskQuota())
						.instances(cfProperties.instances())
						.memory(cfProperties.memory())
						.timeout(cfProperties.timeout())
						.domain(cfProperties.domain())
						.host(cfProperties.hostName())
						.routePath(cfProperties.path())
						.noStart(true)
						.build());

		if (cfProperties.environment() != null) {
			for (Map.Entry<String, String> entry : cfProperties.environment().entrySet()) {
				LOGGER.lifecycle("Setting env variable '{}'", entry.getKey());
				resp = resp.then(cfOperations.applications()
						.setEnvironmentVariable(SetEnvironmentVariableApplicationRequest
								.builder()
								.name(cfProperties.name())
								.variableName(entry.getKey())
								.variableValue(entry.getValue())
								.build()));
			}
		}

		if (cfProperties.services() != null) {
			for (String serviceName : cfProperties.services()) {
				LOGGER.lifecycle("Binding Service '{}'", serviceName);
				resp = resp.then(cfOperations.services()
						.bind(BindServiceInstanceRequest.builder()
								.serviceInstanceName(serviceName)
								.applicationName(cfProperties.name())
								.build()));
			}
		}

		LOGGER.lifecycle("Starting app '{}'", cfProperties.name());
		return resp.then(cfOperations.applications().restart(RestartApplicationRequest
				.builder()
				.name(cfProperties.name()).build()));

	}
}
