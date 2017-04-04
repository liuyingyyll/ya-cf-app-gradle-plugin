package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfServiceDetail;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.CreateServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Responsible for creating a Service Instance
 *
 * @author Biju Kunjummen
 */
public class CfCreateServiceHelper {

    private CfServicesDetailHelper servicesDetailHelper = new CfServicesDetailHelper();
    private static final Logger LOGGER = Logging.getLogger(CfCreateServiceHelper.class);

    public Mono<Void> createService(CloudFoundryOperations cfOperations,
                                    CfServiceDetail cfServiceDetail) {

        Mono<Optional<ServiceInstance>> serviceInstanceMono = servicesDetailHelper
            .getServiceInstanceDetail(cfOperations, cfServiceDetail.instanceName());

        return serviceInstanceMono
            .then(serviceInstanceOpt -> serviceInstanceOpt.map(serviceInstance -> {
                LOGGER.lifecycle(
                    "Existing service with name {} found. This service will not be re-created",
                    cfServiceDetail.instanceName());
                return Mono.empty().then();
            }).orElseGet(() -> {
                LOGGER
                    .lifecycle("Creating service -  instance: {}, service: {}, plan: {}",
                        cfServiceDetail.instanceName(), cfServiceDetail.name(),
                        cfServiceDetail.plan());

                return cfOperations.services().createInstance(
                    CreateServiceInstanceRequest.builder()
                        .serviceInstanceName(cfServiceDetail.instanceName())
                        .serviceName(cfServiceDetail.name())
                        .planName(cfServiceDetail.plan()).build());
            }));

    }
}
