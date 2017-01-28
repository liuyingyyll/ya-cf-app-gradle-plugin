package io.pivotal.services.plugin.cf;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;

import reactor.core.publisher.Mono;

/**
 * Token Provider that simply returns a fixed token back
 *
 * This is intended to be used in scenarios where the token is generated outside of
 * the plugin and then passed to it for deployment scenarios.
 *
 * @author Biju Kunjummen
 */

public class StaticTokenProvider implements TokenProvider {
	private final String token;

	public StaticTokenProvider(String token) {
		this.token = token;
	}

	@Override
	public Mono<String> getToken(ConnectionContext connectionContext) {
		return Mono.just(this.token);
	}
}
