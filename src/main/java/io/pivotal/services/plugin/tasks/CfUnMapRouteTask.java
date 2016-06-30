package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfPushPluginExtension;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

/**
 * Responsible for handling unmap route task.
 *
 * @author Biju Kunjummen
 */
public class CfUnMapRouteTask extends AbstractCfTask {

	@TaskAction
	public void unmapRoute() {
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


		Mono<Void> resp = cfOperations.routes()
				.unmap(UnmapRouteRequest
						.builder()
						.applicationName(getCfApplicationName())
						.domain(getAppDomain())
						.host(getAppHostName()).build());

		resp.block(600_000L);

	}

}
