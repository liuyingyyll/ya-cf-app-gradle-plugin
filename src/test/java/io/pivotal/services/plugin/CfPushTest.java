//package org.bk.cf.plugin;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
//import org.cloudfoundry.client.CloudFoundryClient;
//import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
//import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
//import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
//import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
//import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
//import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
//import org.cloudfoundry.operations.CloudFoundryOperations;
//import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
//import org.cloudfoundry.operations.applications.PushApplicationRequest;
//import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
//import org.junit.Test;
//import reactor.core.publisher.Mono;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//
//public class CfPushTest {
//
//    @Test
//    public void testCfPush() throws Exception{
//        CloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
//                .host("api.local.pcfdev.io")
//                .username("admin")
//                .password("admin")
//                .skipSslValidation(true)
//                .build();
//
//        Mono<ListOrganizationsResponse> orgResp = cfClient.organizations().list(ListOrganizationsRequest.builder().build());
//
//        System.out.println("orgResp = " + orgResp.block().getResources());
//
//        CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
//                .cloudFoundryClient(cfClient)
//                .organization("pcfdev-org")
//                .space("pcfdev-space")
//                .build();
//
////        cfClient.spaces().get(GetSpaceRequest.builder().spaceId("92de32b4-0105-41dc-b899-1fbe1cf8e4c9").build());
//
////        Mono<CreateApplicationResponse> resp = requestCreateApplication(cfClient, "92de32b4-0105-41dc-b899-1fbe1cf8e4c9",
////                "testapp", "staticfile_buildpack", true, 64, 512);
//
//        File file = new File("/Users/bkunjummen/learn/cf-show-env/build/libs/cf-first-sample-0.1.2-SNAPSHOT.jar");
//        try(InputStream ios = new FileInputStream(file)) {
//            Mono<Void> resp = cfOperations.applications()
//                    .push(PushApplicationRequest.builder()
//                            .name("testApp")
//                            .application(ios)
//                            .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
//                            .build());
//            resp.block(300000L);
////        cfClient.applicationsV2().upload(UploadApplicationRequest.builder().)
//        }
//
//
//    }
//}
