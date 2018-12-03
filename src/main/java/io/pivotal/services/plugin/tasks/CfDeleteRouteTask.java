package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfDeleteRouteDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for Deleting a route
 *
 * @author Biju Kunjummen
 */
public class CfDeleteRouteTask extends AbstractCfTask {

    private CfDeleteRouteDelegate deleteRouteDelegate =  new CfDeleteRouteDelegate();

    @TaskAction
    public void deleteRoute() {
        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties cfProperties = getCfProperties();

        Mono<Void> resp = deleteRouteDelegate.deleteRoute(cfOperations, cfProperties);

        resp.block(Duration.ofMillis(defaultWaitTimeout));
    }


    @Override
    public String getDescription() {
        return "Delete a route from Cloud Foundry";
    }
}
