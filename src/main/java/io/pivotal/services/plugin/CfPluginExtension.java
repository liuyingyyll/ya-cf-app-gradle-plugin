package io.pivotal.services.plugin;

import lombok.Data;
import lombok.ToString;
import org.cloudfoundry.Nullable;
import org.cloudfoundry.operations.applications.ApplicationDetail;

import java.util.List;
import java.util.Map;
/**
 * A holder for properties set via a Gradle cfConfig closure
 * <pre>
 *     cfConfig {
 *       ccHost = "api.local.pcfdev.io"
 *       ccUser = "admin"
 *       ccPassword = "admin"
 *       org = "pcfdev-org"
 *       space = "pcfdev-space"
 *       ....
 *       }
 * </pre>
 *
 * @author Biju Kunjummen
 */
@Data
@ToString(exclude="ccPassword")
public class CfPluginExtension {
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
	private Integer stagingTimeout;
	private Integer startupTimeout;
	private String healthCheckType;
	private Integer instances;
	private Integer memory;
	private List<Integer> ports;
	private List<String> services;

	private ApplicationDetail applicationDetail;
}
