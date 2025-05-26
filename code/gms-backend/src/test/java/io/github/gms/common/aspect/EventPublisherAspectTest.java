package io.github.gms.common.aspect;

import io.github.gms.abstraction.AbstractUnitTest;
import io.github.gms.auth.model.GmsUserDetails;
import io.github.gms.common.enums.EventOperation;
import io.github.gms.common.enums.EventTarget;
import io.github.gms.common.model.UserEvent;
import io.github.gms.functions.event.EventService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test of {@link EventPublisherAspect}
 */
class EventPublisherAspectTest extends AbstractUnitTest {

	@Mock
	private EventService service;
	@Mock
	private Clock clock;
	@InjectMocks
	private EventPublisherAspect aspect;

	@Test
	void test_whenAnnotationIsOnTargetClass_thenReturnOk() {
		try (MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
			// arrange
			ReflectionTestUtils.setField(aspect, "service", service);
			setupClock(clock);

			SecurityContext mockContext = mock(SecurityContext.class);
			Authentication mockAuthentication = mock(Authentication.class);
			UserDetails mockUserDetails = mock(UserDetails.class);
			when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
			when(mockContext.getAuthentication()).thenReturn(mockAuthentication);
			contextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockContext);

			// act
			TestController target = new TestController();
			AspectJProxyFactory factory = new AspectJProxyFactory(target);
			factory.addAspect(aspect);
			TestController proxy = factory.getProxy();

			String response = proxy.test();

			// assert
			Assertions.assertThat(response).isEqualTo("OK");

			verifyNoInteractions(service);
		}
	}
	
	@Test
	void test_whenAnnotationIsOnTargetMethod_thenReturnOk() {
		try (MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
			// arrange
			ReflectionTestUtils.setField(aspect, "service", service);
			setupClock(clock);
			SecurityContext mockContext = mock(SecurityContext.class);
			Authentication mockAuthentication = mock(Authentication.class);
			GmsUserDetails mockUserDetails = mock(GmsUserDetails.class);
			when(mockUserDetails.getUserId()).thenReturn(1L);
			when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
			when(mockContext.getAuthentication()).thenReturn(mockAuthentication);
			contextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockContext);

			// act
			TestController target = new TestController();
			AspectJProxyFactory factory = new AspectJProxyFactory(target);
			factory.addAspect(aspect);
			TestController proxy = factory.getProxy();

			String response = proxy.test2();

			// assert
			Assertions.assertThat(response).isEqualTo("OK");
			ArgumentCaptor<UserEvent> userEventCaptor = ArgumentCaptor.forClass(UserEvent.class);
			Mockito.verify(service).saveUserEvent(userEventCaptor.capture());

			UserEvent capturedUserEvent = userEventCaptor.getValue();
			Assertions.assertThat(capturedUserEvent.getOperation()).isEqualTo(EventOperation.LIST);
			Assertions.assertThat(capturedUserEvent.getTarget()).isEqualTo(EventTarget.API_KEY);
		}
	}

	@Test
	void test_whenAuthenticationIsMissing_thenProceedAsNormal() {
		try (MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
			// arrange
			ReflectionTestUtils.setField(aspect, "service", service);
			setupClock(clock);
			SecurityContext mockContext = mock(SecurityContext.class);
			when(mockContext.getAuthentication()).thenReturn(null);
			contextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(mockContext);

			// act
			TestController target = new TestController();
			AspectJProxyFactory factory = new AspectJProxyFactory(target);
			factory.addAspect(aspect);
			TestController proxy = factory.getProxy();

			String response = proxy.test2();

			// assert
			Assertions.assertThat(response).isEqualTo("OK");
			ArgumentCaptor<UserEvent> userEventCaptor = ArgumentCaptor.forClass(UserEvent.class);
			Mockito.verify(service).saveUserEvent(userEventCaptor.capture());

			UserEvent capturedUserEvent = userEventCaptor.getValue();
			Assertions.assertThat(capturedUserEvent.getOperation()).isEqualTo(EventOperation.LIST);
			Assertions.assertThat(capturedUserEvent.getTarget()).isEqualTo(EventTarget.API_KEY);
		}
	}
	
	@Test
	void test_whenAnnotationIsMissing_thenReturnOk() {
		// arrange
		ReflectionTestUtils.setField(aspect, "service", service);
		
		// act
		Test2Controller target = new Test2Controller();
		AspectJProxyFactory factory = new AspectJProxyFactory(target);
		factory.addAspect(aspect);
		Test2Controller proxy = factory.getProxy();
		
		String response = proxy.test();
		
		// assert
		Assertions.assertThat(response).isEqualTo("OK");
		
		response = proxy.test2(1L);
		
		// assert
		Assertions.assertThat(response).isEqualTo("OK");
		Mockito.verify(service, never()).saveUserEvent(any(UserEvent.class));
	}
	
	@Test
	void test_whenMethodsCalled_thenPointcutsDoNothing() {
		assertDoesNotThrow(() -> aspect.allMethod());
		assertDoesNotThrow(() -> aspect.audited());
		assertDoesNotThrow(() -> aspect.restController());
	}
}
