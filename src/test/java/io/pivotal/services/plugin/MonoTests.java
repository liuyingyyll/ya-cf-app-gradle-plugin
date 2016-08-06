package io.pivotal.services.plugin;

import org.junit.Test;
import reactor.core.publisher.Mono;

public class MonoTests {

	@Test
	public void testMono() {
		Mono.just(1).then(Mono.fromSupplier(() -> {
			System.out.println("after empty mono");
			return 1;
		})).block();
	}
}
