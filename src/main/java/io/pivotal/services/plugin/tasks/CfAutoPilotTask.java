package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfAutoPilotDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for an <a href="https://github.com/concourse/autopilot">autpilot</a> style deployment
 *
 * @author Biju Kunjummen
 */
public class CfAutoPilotTask extends AbstractCfTask {


    private CfAutoPilotDelegate autoPilotTaskDelegate = new CfAutoPilotDelegate();

    @TaskAction
    public void runAutopilot() {
        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties originalProperties = getCfProperties();


        Mono<Void> resp = autoPilotTaskDelegate.runAutopilot(getProject(), cfOperations, originalProperties);

        resp.block(Duration.ofMillis(defaultWaitTimeout));
    }

    @Override
    public String getDescription() {
        return "Push an Application in a no downtime Autopilot mode";
    }

}
