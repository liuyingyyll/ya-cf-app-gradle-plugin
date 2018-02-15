package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfUserProvidedServiceDetail;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.CreateUserProvidedServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for creating a User Provided Service Instance
 *
 * @author Biju Kunjummen
 */
public class CfCreateUserProvidedServiceHelper {

    private CfServicesDetailHelper servicesDetailHelper = new CfServicesDetailHelper();
    private static final Logger LOGGER = Logging.getLogger(CfCreateServiceHelper.class);

    public Mono<Void> createUserProvidedService(CloudFoundryOperations cfOperations,
                                                CfUserProvidedServiceDetail cfUserProvidedServiceDetail) {

        Mono<Optional<ServiceInstance>> serviceInstanceMono = servicesDetailHelper
            .getServiceInstanceDetail(cfOperations,
                cfUserProvidedServiceDetail.instanceName());

        return serviceInstanceMono
            .flatMap(serviceInstanceOpt -> serviceInstanceOpt.map(serviceInstance -> {
                LOGGER.lifecycle(
                    "Existing service with name {} found. This service will not be re-created",
                    cfUserProvidedServiceDetail.instanceName());
                return Mono.empty().then();
            }).orElseGet(() -> {
                LOGGER.lifecycle("Creating user provided service -  instance: {}",
                    cfUserProvidedServiceDetail.instanceName());
                return cfOperations.services().createUserProvidedInstance(
                    CreateUserProvidedServiceInstanceRequest.builder()
                        .name(cfUserProvidedServiceDetail.instanceName())
                        .credentials(cfUserProvidedServiceDetail.credentials()).build());
            }));
    }
}
