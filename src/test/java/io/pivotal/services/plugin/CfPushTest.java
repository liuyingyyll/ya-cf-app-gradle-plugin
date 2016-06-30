package io.pivotal.services.plugin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class CfPushTest {

    @Test
    public void testCfPush() throws Exception{
        CloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
                .host("api.local.pcfdev.io")
                .username("admin")
                .password("admin")
                .skipSslValidation(true)
                .build();


        CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cfClient)
                .organization("pcfdev-org")
                .space("pcfdev-space")
                .build();



        File file = new File("/Users/bkunjummen/learn/cf-show-env/build/libs/cf-first-sample-0.1.2-SNAPSHOT.jar");
        try(InputStream ios = new FileInputStream(file)) {
            Mono<Void> resp = cfOperations.applications()
                    .push(PushApplicationRequest.builder()
                            .name("testApp")
                            .application(ios)
                            .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                            .build());
            resp.block(300000L);
//        cfClient.applicationsV2().upload(UploadApplicationRequest.builder().)
        }


    }

}
