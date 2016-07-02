package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
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

		CloudFoundryOperations cfOperations = getCfOperations();

		File file = new File(getFilePath());
		try {
			try (InputStream ios = new FileInputStream(file)) {
				Mono<Void> resp = cfOperations.applications()
						.push(PushApplicationRequest.builder()
								.name(getCfApplicationName())
								.application(ios)
								.buildpack(getBuildpack())
								.command(extension.getCommand())
								.diskQuota(extension.getDiskQuota())
								.instances(getInstances())
								.memory(getMemory())
								.timeout(extension.getHealthCheckTimeout())
								.domain(getAppDomain())
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

	@Override
	public String getDescription() {
		return "Pushes an Application to Cloud Foundry";
	}
}
