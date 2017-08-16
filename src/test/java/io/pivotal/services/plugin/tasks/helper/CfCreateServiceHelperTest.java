package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfServiceDetail;
import io.pivotal.services.plugin.ImmutableCfServiceDetail;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.cloudfoundry.operations.services.ServiceInstanceType;
import org.cloudfoundry.operations.services.Services;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfCreateServiceHelperTest {

    private CfCreateServiceHelper cfCreateServiceHelper = new CfCreateServiceHelper();

    @Test
    public void testCreateServiceWithAnExistingService() {
        CloudFoundryOperations cfOps = mock(CloudFoundryOperations.class);
        Services mockServices = mock(Services.class);

        when(mockServices
            .getInstance(any(GetServiceInstanceRequest.class)))
            .thenReturn(Mono.just(ServiceInstance.builder()
                .id("inst")
                .type(ServiceInstanceType.MANAGED)
                .name("inst")
                .build()));

        when(cfOps.services()).thenReturn(mockServices);

        CfServiceDetail cfServiceDetail = ImmutableCfServiceDetail
            .builder().name("testName")
            .plan("testPlan")
            .instanceName("inst")
            .build();
        
        
        Mono<ServiceInstance> createServiceResult = this.cfCreateServiceHelper.createService(cfOps, cfServiceDetail);

        StepVerifier.create(createServiceResult)
            .expectNextMatches(instance -> instance != null)
            .verifyComplete();
    }

    @Test
    public void testCreateServiceWithNoExistingService() {
        CloudFoundryOperations cfOps = mock(CloudFoundryOperations.class);
        Services mockServices = mock(Services.class);

        when(mockServices
            .getInstance(any(GetServiceInstanceRequest.class)))
            .thenReturn(Mono.empty());

        when(cfOps.services()).thenReturn(mockServices);

        CfServiceDetail cfServiceDetail = ImmutableCfServiceDetail
            .builder().name("testName")
            .plan("testPlan")
            .instanceName("inst")
            .build();


        Mono<ServiceInstance> createServiceResult = this.cfCreateServiceHelper.createService(cfOps, cfServiceDetail);

        StepVerifier.create(createServiceResult)
            .verifyComplete();
    }
}
