package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfAppDetailsDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * Responsible for exposing the details of an application
 *
 * @author Biju Kunjummen
 */
public class CfAppDetailsTask extends AbstractCfTask {

    private CfAppDetailsDelegate detailsTaskDelegate = new CfAppDetailsDelegate();

    @TaskAction
    public void appDetails() {

        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties cfProperties = getCfProperties();

        Mono<Optional<ApplicationDetail>> resp = detailsTaskDelegate.getAppDetails(cfOperations, cfProperties);

        Optional<ApplicationDetail> applicationDetail = resp.block(Duration.ofMillis(defaultWaitTimeout));

        setApplicationDetail(applicationDetail.orElseThrow(() -> new IllegalArgumentException("No application found")));
    }

    private void setApplicationDetail(ApplicationDetail applicationDetail) {
        this.getExtension().setApplicationDetail(applicationDetail);
    }

    @Override
    public String getDescription() {
        return "Get the application detail from Cloud Foundry";
    }
}
