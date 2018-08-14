package io.pivotal.services.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.junit.Test;

public class CfManifestUtilTest {
    @Test
    public void testIfServicesAreCorrect() {
        final String A_SERVICE = "a-service-1";
        CfProperties cfProperties = mock(CfProperties.class);
        when(cfProperties.name()).thenReturn("because-name-cant-be-null");
        when(cfProperties.services()).thenReturn(Collections.singletonList(A_SERVICE));
        ApplicationManifest manifest = CfManifestUtil.convert(cfProperties);
        assertThat(manifest.getServices().size()).isEqualTo(1);
        assertThat(manifest.getServices().get(0)).isEqualTo(A_SERVICE);
    }

    @Test
    public void testIfRoutesAreCorrect() {
        final String A_ROUTE = "a-route-1.domain.com";
        CfProperties cfProperties = mock(CfProperties.class);
        when(cfProperties.name()).thenReturn("because-name-cant-be-null");
        when(cfProperties.routes()).thenReturn(Collections.singletonList(A_ROUTE));
        ApplicationManifest manifest = CfManifestUtil.convert(cfProperties);
        assertThat(manifest.getRoutes().size()).isEqualTo(1);
        assertThat(manifest.getRoutes().get(0).getRoute()).isEqualTo(A_ROUTE);
    }
}
