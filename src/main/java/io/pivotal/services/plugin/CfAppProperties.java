package io.pivotal.services.plugin;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.List;
import java.util.Map;

/**
 * A copy of the CfAppPluginExtension, this will be used for driving the various tasks instead of the extension
 * that is set from gradle.
 */

@Value
@Builder
@ToString(exclude="ccPassword")
public class CfAppProperties {
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

	private Integer stagingTimeout = 15;
	private Integer startupTimeout = 5;

}
