package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.RestartApplicationRequest;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for restarting an app
 *
 * @author Biju Kunjummen
 */
public class CfAppRestartTask extends AbstractCfTask {

    @TaskAction
    public void restartApp() {

        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties cfProperties = getCfProperties();

        Mono<Void> resp = cfOperations.applications()
            .restart(RestartApplicationRequest.builder().name(cfProperties.name()).build());

        resp.block(Duration.ofMillis(defaultWaitTimeout));

    }

    @Override
    public String getDescription() {
        return "Restart an Application";
    }

}
