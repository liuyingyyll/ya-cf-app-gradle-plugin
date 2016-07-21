package io.pivotal.services.plugin;

import lombok.Data;
import lombok.ToString;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.operations.applications.ApplicationDetail;

import java.util.List;
import java.util.Map;

/**
 * Holds configuration related to cf-push
 * @author Biju Kunjummen
 */
@Data
@ToString(exclude="ccPassword")
public class CfPushPluginExtension {
	private String ccHost;
	private String ccUser;
	private String ccPassword;

	private String org;
	private String space;

	private String name;
	private String filePath;
	private String hostName;
	private String domain;
	private String path;


	private String state;
	private String buildpack;
	private String command;
	private Boolean console;
	private Boolean debug;
	private String detectedStartCommand;
	private Integer diskQuota;
	private Boolean enableSsh;
	private Map<String, String> environment;
	private Integer timeout;
	private String healthCheckType;
	private Integer instances;
	private Integer memory;
	private List<Integer> ports;
	private List<String> services;


	private ApplicationDetail applicationDetail;
}
