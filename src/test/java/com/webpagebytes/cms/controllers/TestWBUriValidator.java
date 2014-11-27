package com.webpagebytes.cms.controllers;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.controllers.WPBErrors;
import com.webpagebytes.cms.controllers.UriValidator;

@RunWith(PowerMockRunner.class)
public class TestWBUriValidator {

private UriValidator uriValidator;
private WPBUri wburi;
private Map<String, String> errorsContainer;
@Before
public void setUp()
{
	wburi = new WPBUri();
	errorsContainer = new HashMap<String, String>();
	uriValidator = new UriValidator();
}

@Test
public void test_validateCreateWBUri_empty()
{
	errorsContainer.put("uri", WPBErrors.ERROR_URI_LENGTH);
	errorsContainer.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
	errorsContainer.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
	errorsContainer.put("enabled", WPBErrors.ERROR_INVALID_VALUE);
	
	Map<String, String> errors1 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors1));
	
	//now put empty string
	wburi.setUri("");
	Map<String, String> errors2 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors2));
	
}

@Test
public void test_validateCreateWBUri_wrongUriFirstCharacter()
{
	errorsContainer.put("uri", WPBErrors.ERROR_URI_START_CHAR);
	wburi.setHttpOperation("GET");
	wburi.setUri("test");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("123");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_OK_uri()
{
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_NoResourceType()
{
	errorsContainer.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}
@Test
public void test_validateCreateWBUri_BadResourceType()
{
	errorsContainer.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("abc");
	wburi.setResourceType(400); // this is a bad type
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_okResourceType()
{
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("abc");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_TEXT);
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	Map<String, String> errors1 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors1));

	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE); 
	Map<String, String> errors2 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors2));
	
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_URL_CONTROLLER); 
	Map<String, String> errors3 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors3));
}

@Test
public void test_validateCreateWBUri_okController()
{
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("abc");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_TEXT);
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	wburi.setControllerClass("com.test.test");
	Map<String, String> errors1 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors1));
}

@Test
public void test_validateCreateWBUri_invalidController()
{
	errorsContainer.put("controllerClass", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("abc");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_TEXT);
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	wburi.setControllerClass("Test*");
	Map<String, String> errors1 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors1));
	
	String controller = "A";
	for (int i = 0; i< UriValidator.MAX_CONTROLLER_LENGHT; i++)
	{
		controller += 'x';
	}
	
	wburi.setControllerClass(controller);
	Map<String, String> errors2 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors2));
	
}

@Test
public void test_validateCreateWBUri_NoResourceExternalKey()
{
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_ZeroResourceExternalKey()
{
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setEnabled(0);
	wburi.setResourceExternalKey("");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}


@Test
public void test_validateUpdateWBUri_NoResourceType()
{
	errorsContainer.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setPrivkey(1L);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setEnabled(1);
	wburi.setResourceExternalKey("abc");
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}
@Test
public void test_validateUpdateWBUri_BadResourceType()
{
	errorsContainer.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setPrivkey(1L);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	wburi.setResourceType(4); // this is a bad type

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_okResourceType()
{
	wburi.setPrivkey(1L);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setExternalKey("xyz");
	wburi.setEnabled(1);
	wburi.setResourceExternalKey("abc");

	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	Map<String, String> errors2 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors2));

	wburi.setResourceType(WPBUri.RESOURCE_TYPE_TEXT);
	Map<String, String> errors1 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors1));
	
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_URL_CONTROLLER);
	Map<String, String> errors3 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors3));


}

@Test
public void test_validateUpdateWBUri_NoResourceExternalKey()
{
	wburi.setHttpOperation("GET");
	wburi.setPrivkey(1L);
	wburi.setUri("/test");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_ZeroResourceExternalKey()
{
	wburi.setPrivkey(1L);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceExternalKey("");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}


@Test
public void test_validateCreateWBUri_uriTooLong()
{
	errorsContainer.put("uri", WPBErrors.ERROR_URI_LENGTH);
	wburi.setHttpOperation("GET");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	String uri = "/a";
	for(int i =0; i< UriValidator.MAX_URI_LENGHT;i++)
	{
		uri = uri + "a";
	}
	wburi.setUri(uri);
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_emptyHttpOperation()
{
	errorsContainer.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	Map<String, String> errors1 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors1));

	// now set empty httpOperation
	wburi.setHttpOperation("");
	Map<String, String> errors2 = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors2));
	
}

@Test
public void test_validateCreateWBUri_invalidHttpOperation()
{
	errorsContainer.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setHttpOperation("ABC");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_cantSpecifyLastModified()
{
	errorsContainer.put("key", WPBErrors.ERROR_CANT_SPECIFY_KEY);
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setLastModified( new Date());
	wburi.setPrivkey(10L);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

///
@Test
public void test_validateUpdateWBUri_empty()
{
	errorsContainer.put("key", WPBErrors.ERROR_NO_KEY);
	errorsContainer.put("uri", WPBErrors.ERROR_URI_LENGTH);
	errorsContainer.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
	errorsContainer.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
	errorsContainer.put("externalKey", WPBErrors.ERROR_INVALID_VALUE);
	errorsContainer.put("enabled", WPBErrors.ERROR_INVALID_VALUE);
	
	Map<String, String> errors1 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors1));
	
	//now put empty string
	wburi.setUri("");
	Map<String, String> errors2 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors2));
	
}

@Test
public void test_validateUpdateWBUri_wrongUriFirstCharacter()
{
	errorsContainer.put("uri", WPBErrors.ERROR_URI_START_CHAR);
	wburi.setHttpOperation("GET");
	wburi.setPrivkey(10L);
	wburi.setUri("test");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_OK_uri()
{
	wburi.setPrivkey(10L);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setExternalKey("xyz");
	wburi.setEnabled(1);
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_uriTooLong()
{
	wburi.setPrivkey(10L);
	errorsContainer.put("uri", WPBErrors.ERROR_URI_LENGTH);
	wburi.setHttpOperation("GET");
	
	String uri = "/a";
	for(int i =0; i< UriValidator.MAX_URI_LENGHT;i++)
	{
		uri = uri + "a";
	}
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_emptyHttpOperation()
{
	wburi.setPrivkey(10L);
	errorsContainer.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	
	Map<String, String> errors1 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors1));

	// now set empty httpOperation
	wburi.setHttpOperation("");
	Map<String, String> errors2 = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors2));
	
}

@Test
public void test_validateUpdateWBUri_invalidHttpOperation()
{
	wburi.setPrivkey(10L);
	errorsContainer.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
	wburi.setHttpOperation("ABC");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_cantSpecifyLastModify()
{
	wburi.setPrivkey(10L);
	errorsContainer.put("lastModified", WPBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);	
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setLastModified( new Date());
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_OK()
{
	wburi.setPrivkey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setExternalKey("xyz");
	wburi.setEnabled(1);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}


@Test
public void test_validateCreateWBUri_uri_goodformat()
{
	wburi.setHttpOperation("GET");	
	String uri = "/test/aaa/bb-cc/aaa_123/123/AAA/~1";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_uri_badformat1()
{
	wburi.setHttpOperation("GET");	
	String uri = "/t?est/aaa/bb-cc/aaa_123/123/AAA/~1";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(0);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("uri", WPBErrors.ERROR_INVALID_VALUE);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_uri_badformat2()
{
	wburi.setHttpOperation("GET");	
	String uri = "/+?&";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("uri", WPBErrors.ERROR_INVALID_VALUE);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_uri_badformat1()
{
	wburi.setPrivkey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/t?est/aaa/bb-cc/aaa_123/123/AAA/~1";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("uri", WPBErrors.ERROR_INVALID_VALUE);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_uri_badformat2()
{
	wburi.setPrivkey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/+?&";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");
	wburi.setEnabled(1);
	wburi.setExternalKey("xyz");

	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("uri", WPBErrors.ERROR_INVALID_VALUE);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_noKey()
{
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setResourceType(WPBUri.RESOURCE_TYPE_FILE);
	wburi.setResourceExternalKey("abc");

	Map<String, String> errors1 = uriValidator.validateUpdate(wburi);
	assertTrue( errors1.get("key").compareTo(WPBErrors.ERROR_NO_KEY) == 0);

	wburi.setPrivkey(0L);
	Map<String, String> errors2 = uriValidator.validateUpdate(wburi);
	assertTrue( errors2.get("key").compareTo(WPBErrors.ERROR_NO_KEY) == 0);
}


}
