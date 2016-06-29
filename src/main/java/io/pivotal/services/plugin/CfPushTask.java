package io.pivotal.services.plugin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationRequest;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responsible for cf push.
 */
public class CfPushTask extends DefaultTask {

    @TaskAction
    public void push() {
        CfPushPluginExtension extension = getProject().getExtensions().findByType(CfPushPluginExtension.class);
        CloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
                .host(extension.getHost())
                .username(extension.getUser())
                .password(extension.getPassword())
                .skipSslValidation(true)
                .build();


        CloudFoundryOperations cfOperations = DefaultCloudFoundryOperations.builder()
                .cloudFoundryClient(cfClient)
                .organization(extension.getOrg())
                .space(extension.getSpace())
                .build();

        File file = new File(extension.getPath());
        try {
            try (InputStream ios = new FileInputStream(file)) {
                Mono<Void> resp = cfOperations.applications()
                        .push(PushApplicationRequest.builder()
                                .name(extension.getName())
                                .application(ios)
                                .buildpack(extension.getBuildpack())
                                .command(extension.getCommand())
                                .diskQuota(extension.getDiskQuota())
                                .instances(extension.getInstances())
                                .memory(extension.getMemory())
                                .timeout(extension.getHealthCheckTimeout())
                                .build());
                resp.block(300_000L);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
