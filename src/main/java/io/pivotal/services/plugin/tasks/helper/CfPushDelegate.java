package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfManifestUtil;
import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfServiceDetail;
import io.pivotal.services.plugin.CfUserProvidedServiceDetail;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.PushApplicationManifestRequest;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Responsible for cf-push, since this functionality is going to be called from a few places this logic has been
 * centralized here.
 *
 * @author Biju Kunjummen
 * @author Gabriel Couto
 */
public class CfPushDelegate {

    private static final Logger LOGGER = Logging.getLogger(CfPushDelegate.class);

    private CfCreateUserProvidedServiceHelper cfCreateUserProvidedServiceHelper = new CfCreateUserProvidedServiceHelper();
    private CfCreateServiceHelper cfCreateServiceHelper = new CfCreateServiceHelper();

    public Mono<Void> push(CloudFoundryOperations cfOperations, CfProperties cfProperties) {
        Path path = Paths.get(cfProperties.filePath());

        if (path.toFile().exists()) {
            Mono<Void> resp = Mono.empty();

            if (cfProperties.cfServices() != null) {
                for (CfServiceDetail service : cfProperties.cfServices()) {
                    resp = resp.then(cfCreateServiceHelper.createService(cfOperations, service)).then();
                }
            }

            if (cfProperties.cfUserProvidedServices() != null) {
                for (CfUserProvidedServiceDetail service : cfProperties.cfUserProvidedServices()) {
                    resp = resp.then(cfCreateUserProvidedServiceHelper.createUserProvidedService(cfOperations, service));
                }
            }

            return resp.then(Mono.defer(() -> {
                LOGGER.lifecycle("Pushing app '{}'", cfProperties.name());
                return cfOperations.applications()
                    .pushManifest(PushApplicationManifestRequest.builder()
                        .manifest(CfManifestUtil.convert(cfProperties))
                        .build()
                    );
            }));
        } else {
            throw new RuntimeException("Missing file : " + path);
        }

    }
}
