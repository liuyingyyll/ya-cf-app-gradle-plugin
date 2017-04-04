package io.pivotal.services.plugin.tasks.helper;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for getting the service instance given a service name
 */
public class CfServicesDetailHelper {
    private static final Logger LOGGER = Logging.getLogger(CfServicesDetailHelper.class);

    public Mono<Optional<ServiceInstance>> getServiceInstanceDetail(CloudFoundryOperations cfOperations,
                                                                    String serviceName) {

        LOGGER.lifecycle("Checking details of service '{}'", serviceName);
        Mono<ServiceInstance> serviceInstanceMono = cfOperations.services()
            .getInstance(
                GetServiceInstanceRequest.builder()
                    .name(serviceName)
                    .build());

        return serviceInstanceMono
            .map(serviceInstance -> Optional.ofNullable(serviceInstance))
            .otherwise(Exception.class, e -> Mono.just(Optional.empty()));
    }

}
