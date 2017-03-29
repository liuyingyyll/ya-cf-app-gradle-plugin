package io.pivotal.services.plugin;

import org.immutables.value.Value;

import java.util.Map;

/**
 * Details of a user provided service instance
 * 
 * @author Biju Kunjummen
 */

@Value.Immutable(copy = true)
public abstract class CfUserProvidedServiceDetail {
	public abstract String instanceName();
	public abstract Map<String, String> credentials();
}
