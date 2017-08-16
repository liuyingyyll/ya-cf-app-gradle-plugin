package io.pivotal.services.plugin.tasks.helper;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.cloudfoundry.operations.services.ServiceInstanceType;
import org.cloudfoundry.operations.services.Services;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class CfServicesDetailHelperTest {

    private CfServicesDetailHelper cfServicesDetailHelper;

    @Before
    public void setUp() {
        this.cfServicesDetailHelper = new CfServicesDetailHelper();
    }

    @Test
    public void testGetServiceDetails() {
        CloudFoundryOperations cfOps = mock(CloudFoundryOperations.class);
        Services mockServices = mock(Services.class);

        when(mockServices
            .getInstance(any(GetServiceInstanceRequest.class)))
            .thenReturn(Mono.just(ServiceInstance.builder()
                .id("id")
                .type(ServiceInstanceType.MANAGED)
                .name("test")
                .build()));

        when(cfOps.services()).thenReturn(mockServices);

        Mono<Optional<ServiceInstance>> serviceDetailMono =
            this.cfServicesDetailHelper.getServiceInstanceDetail(cfOps, "srvc");

        StepVerifier.create(serviceDetailMono).expectNextMatches(serviceInstance ->
            serviceInstance.isPresent());
    }
}
