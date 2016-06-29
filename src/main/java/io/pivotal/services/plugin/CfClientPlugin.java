package io.pivotal.services.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Root of the Cloud Foundry Client Plugin
 */
public class CfClientPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("cfpush", CfPushTask.class);
        project.getExtensions().create("cfPushConfig", CfPushPluginExtension.class);
    }
}
