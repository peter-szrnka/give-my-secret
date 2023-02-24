package io.github.gms.abstraction;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
//@ExtendWith(MockitoExtension.class)
public abstract class AbstractUnitTest {

	protected static void setupClock(Clock clock) {
		when(clock.instant()).thenReturn(Instant.now());
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
	}
}