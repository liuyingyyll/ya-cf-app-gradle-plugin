package io.pivotal.services.plugin;

import lombok.Data;
import org.cloudfoundry.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Holds configuration related to cf-push
 * @author Biju Kunjummen
 */
@Data
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


	private String state;
	private String buildpack;
	private String command;
	private Boolean console;
	private Boolean debug;
	private String detectedStartCommand;
	private Integer diskQuota;
	private Boolean enableSsh;
	private Map<String, String> environment;
	private Integer healthCheckTimeout;
	private String healthCheckType;
	private Integer instances;
	private Integer memory;
	private List<Integer> ports;
	private List<String> services;
}
