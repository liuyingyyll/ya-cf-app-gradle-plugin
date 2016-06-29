package io.pivotal.services.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CfPushClientPluginTests {
    @Test
    public void checkCfPushPlugin() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("cf-push");
        assertThat(project.getTasks().getAt("cfpush") instanceof CfPushTask).isTrue();
    }
}