package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfCreateServiceHelper;
import io.pivotal.services.plugin.tasks.helper.CfCreateUserProvidedServiceHelper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Responsible for creating the list of services in 
 *
 * @author Biju Kunjummen
 */
public class CfCreateServicesTask extends AbstractCfTask {

	private CfCreateServiceHelper createServiceHelper = new CfCreateServiceHelper();
	private CfCreateUserProvidedServiceHelper userProvidedServiceHelper = new CfCreateUserProvidedServiceHelper();

	@TaskAction
	public void cfCreateServiceTask() {

		CloudFoundryOperations cfOperations = getCfOperations();
		CfProperties cfProperties = getCfProperties();

		List<Mono<Void>> createServicesResult = cfProperties.cfServices()
			.stream()
			.map(service -> createServiceHelper.createService(cfOperations, service))
			.collect(Collectors.toList());

		List<Mono<Void>> createUserProvidedServicesResult = cfProperties.cfUserProvidedServices()
			.stream()
			.map(service -> userProvidedServiceHelper.createUserProvidedService(cfOperations, service))
			.collect(Collectors.toList());

		Flux.merge(createServicesResult).toIterable().forEach(r -> {});
		Flux.merge(createUserProvidedServicesResult).toIterable().forEach(r -> {});
		
	
	}

	private void setApplicationDetail(ApplicationDetail applicationDetail) {
		this.getExtension().setApplicationDetail(applicationDetail);
	}

	@Override
	public String getDescription() {
		return "Create a set of services";
	}
}
