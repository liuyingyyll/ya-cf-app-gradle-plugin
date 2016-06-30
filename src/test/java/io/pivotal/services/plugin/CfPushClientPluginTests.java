package io.pivotal.services.plugin;

import io.pivotal.services.plugin.tasks.*;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CfPushClientPluginTests {
    @Test
    public void checkCfPushPlugin() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("cf-push");
        assertThat(project.getTasks().getAt("cf-push") instanceof CfPushTask).isTrue();
        assertThat(project.getTasks().getAt("cf-map-route") instanceof CfMapRouteTask).isTrue();
        assertThat(project.getTasks().getAt("cf-unmap-route") instanceof CfUnMapRouteTask).isTrue();
        assertThat(project.getTasks().getAt("cf-delete-app") instanceof CfDeleteAppTask).isTrue();
        assertThat(project.getTasks().getAt("cf-delete-route") instanceof CfDeleteRouteTask).isTrue();
    }
}