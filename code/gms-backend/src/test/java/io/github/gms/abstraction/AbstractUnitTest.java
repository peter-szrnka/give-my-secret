package io.github.gms.abstraction;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractUnitTest {
	
	@Mock
	protected Clock clock;

	protected void setupClock() {
		when(clock.instant()).thenReturn(Instant.now());
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
	}
}