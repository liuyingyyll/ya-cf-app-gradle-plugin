package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfAppPluginExtension;
import io.pivotal.services.plugin.CfAppProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.cloudfoundry.operations.applications.SetEnvironmentVariableApplicationRequest;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

/**
 * Responsible for cf-push, since this functionality is going to be called from a few places this logic has been
 * centralized here.
 *
 * @author Biju Kunjummen
 */
public class CfPushDelegate {

	public Mono<Void> push(CloudFoundryOperations cfOperations, CfAppProperties cfAppProperties) {
		Path path = Paths.get(cfAppProperties.getFilePath());
		try {
			Mono<Void> resp = cfOperations.applications()
					.push(PushApplicationRequest.builder()
							.name(cfAppProperties.getName())
							.application(path)
							.buildpack(cfAppProperties.getBuildpack())
							.command(cfAppProperties.getCommand())
							.diskQuota(cfAppProperties.getDiskQuota())
							.instances(cfAppProperties.getInstances())
							.memory(cfAppProperties.getMemory())
							.timeout(cfAppProperties.getTimeout())
							.domain(cfAppProperties.getDomain())
							.host(cfAppProperties.getHostName())
							.routePath(cfAppProperties.getPath())
							.noStart(true)
							.build());

			if (cfAppProperties.getEnvironment() != null) {
				for (Map.Entry<String, String> entry : cfAppProperties.getEnvironment().entrySet()) {
					resp = resp.then(cfOperations.applications()
							.setEnvironmentVariable(SetEnvironmentVariableApplicationRequest
									.builder()
									.name(cfAppProperties.getName())
									.variableName(entry.getKey())
									.variableValue(entry.getValue())
									.build()));
				}
			}

			if (cfAppProperties.getServices() != null) {
				for (String serviceName : cfAppProperties.getServices()) {
					resp = resp.then(cfOperations.services()
							.bind(BindServiceInstanceRequest.builder()
									.serviceInstanceName(serviceName)
									.applicationName(cfAppProperties.getName())
									.build()));
				}
			}

			return resp.then(cfOperations.applications().restart(RestartApplicationRequest
					.builder()
					.name(cfAppProperties.getName()).build()));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
