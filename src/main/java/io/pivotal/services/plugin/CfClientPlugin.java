package io.pivotal.services.plugin;

import io.pivotal.services.plugin.tasks.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Root of the Cloud Foundry Client Plugin
 *
 * @author Biju Kunjummen
 */
public class CfClientPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getTasks().create("cf-push", CfPushTask.class);
		project.getTasks().create("cf-map-route", CfMapRouteTask.class);
		project.getTasks().create("cf-unmap-route", CfUnMapRouteTask.class);
		project.getTasks().create("cf-delete-app", CfDeleteAppTask.class);
		project.getTasks().create("cf-delete-route", CfDeleteRouteTask.class);
		project.getTasks().create("cf-rename-app", CfRenameAppTask.class);
		project.getTasks().create("cf-get-app-manifest", CfAppDetailsTask.class);
		project.getExtensions().create("cfConfig", CfPushPluginExtension.class);
	}
}
