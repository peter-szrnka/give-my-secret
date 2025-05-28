package io.github.gms.util;

/**
 * @author Peter Szrnka
 */
public interface TestConstants {

	String TAG_INTEGRATION_TEST = "IntegrationTest";
	String TAG_SECURITY_TEST = "SecurityTest";

	String URL_INFO_STATUS = "/info/status";
	String URL_INFO_ME = "/info/me";

	// TestedMethod annotation values
	String SAVE = "save";
	String LIST = "list";
	String GET_BY_ID = "getById";
	String DELETE = "delete";
	String GET_VALUE = "getValue";
	String TOGGLE = "toggle";
	String ROTATE_SECRET = "rotateSecret";
	String TEST = "test";
}
