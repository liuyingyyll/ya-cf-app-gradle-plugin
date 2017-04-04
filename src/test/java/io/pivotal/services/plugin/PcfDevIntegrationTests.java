package io.pivotal.services.plugin;

import io.pivotal.services.plugin.tasks.helper.CfAppDetailsDelegate;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Category(IntegrationTest.class)
public class PcfDevIntegrationTests {

    @Test
    public void getAppDetailTests() {
        ConnectionContext connectionContext = DefaultConnectionContext.builder()
            .apiHost("api.local.pcfdev.io")
            .skipSslValidation(true)
            .build();

        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
            .password("admin")
            .username("admin")
            .build();

        CloudFoundryClient cfClient = ReactorCloudFoundryClient.builder()
            .connectionContext(connectionContext)
            .tokenProvider(tokenProvider)
            .build();

        CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
            .cloudFoundryClient(cfClient)
            .organization("pcfdev-org")
            .space("pcfdev-space")
            .build();

        CfAppDetailsDelegate appDetailsTaskDelegate = new CfAppDetailsDelegate();
        CfProperties cfAppProps = envDetails();
        Mono<Optional<ApplicationDetail>> applicationDetailMono = appDetailsTaskDelegate
            .getAppDetails(cfOperations, cfAppProps);


//		Mono<Void> resp = applicationDetailMono.then(applicationDetail -> Mono.fromSupplier(() -> {
//			return 1;
//		})).then();
//
//		resp.block();
//		ApplicationDetail applicationDetail = applicationDetailMono.block(Duration.ofMillis(5000));
//		System.out.println("applicationDetail = " + applicationDetail);
    }

    private CfProperties envDetails() {
        return ImmutableCfProperties.builder()
            .ccHost("api.local.pcfdev.io")
            .ccUser("admin")
            .ccPassword("admin")
            .org("pcfdev-org")
            .space("pcfdev-space")
            .name("cf-show-env")
            .build();
    }
}
