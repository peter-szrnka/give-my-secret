package io.github.gms.abstraction;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.SneakyThrows;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractUnitTest {

	protected static void setupClock(Clock clock) {
		when(clock.instant()).thenReturn(Instant.now());
		when(clock.getZone()).thenReturn(ZoneOffset.UTC);
	}

	@SneakyThrows
	protected static <T> void assertPrivateConstructor(Class<T> clazz) {
		Constructor<T> constructor = clazz.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
}