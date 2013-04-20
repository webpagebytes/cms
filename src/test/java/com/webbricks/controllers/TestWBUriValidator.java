package com.webbricks.controllers;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webbricks.cmsdata.WBUri;
import com.webbricks.controllers.WBErrors;
import com.webbricks.controllers.WBUriValidator;

@RunWith(PowerMockRunner.class)
public class TestWBUriValidator {

private WBUriValidator uriValidator;
private WBUri wburi;
private Map<String, String> errorsContainer;
@Before
public void setUp()
{
	wburi = new WBUri();
	errorsContainer = new HashMap<String, String>();
	uriValidator = new WBUriValidator();
}

@Test
public void test_validateCreateWBUri_empty()
{
	errorsContainer.put("uri", WBErrors.ERROR_URI_LENGTH);
	errorsContainer.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGENAME_LENGTH);
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
	errorsContainer.put("uri", WBErrors.ERROR_URI_START_CHAR);
	wburi.setHttpOperation("GET");
	wburi.setUri("test");
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_OK_uri()
{
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_uriTooLong()
{
	errorsContainer.put("uri", WBErrors.ERROR_URI_LENGTH);
	wburi.setHttpOperation("GET");
	wburi.setPageName("test");
	String uri = "/a";
	for(int i =0; i< WBUriValidator.MAX_URI_LENGHT;i++)
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
	errorsContainer.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
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
	errorsContainer.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
	wburi.setHttpOperation("ABC");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_cantSpecify()
{
	errorsContainer.put("key", WBErrors.ERROR_CANT_SPECIFY_KEY);
	errorsContainer.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);	
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setLastModified( new Date());
	wburi.setKey(10L);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

///
@Test
public void test_validateUpdateWBUri_empty()
{
	errorsContainer.put("key", WBErrors.ERROR_NO_KEY);
	errorsContainer.put("uri", WBErrors.ERROR_URI_LENGTH);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGENAME_LENGTH);
	errorsContainer.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
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
	errorsContainer.put("uri", WBErrors.ERROR_URI_START_CHAR);
	wburi.setHttpOperation("GET");
	wburi.setKey(10L);
	wburi.setUri("test");
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_OK_uri()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");
	wburi.setUri("/test");
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_uriTooLong()
{
	wburi.setKey(10L);
	errorsContainer.put("uri", WBErrors.ERROR_URI_LENGTH);
	wburi.setHttpOperation("GET");
	
	String uri = "/a";
	for(int i =0; i< WBUriValidator.MAX_URI_LENGHT;i++)
	{
		uri = uri + "a";
	}
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_emptyHttpOperation()
{
	wburi.setKey(10L);
	errorsContainer.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
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
	wburi.setKey(10L);
	errorsContainer.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
	wburi.setHttpOperation("ABC");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_cantSpecify()
{
	wburi.setKey(10L);
	errorsContainer.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);	
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
	wburi.setLastModified( new Date());
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_pageName_OK()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_pageName_OK()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_pageName_tooshort()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGENAME_LENGTH);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_pageName_tooshort()
{
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	wburi.setPageName("");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGENAME_LENGTH);
	assertTrue( errorsContainer.equals(errors));
}
@Test
public void test_validateUpdateWBUri_pageName_toolong()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	String name = "";
	for(int i = 0; i<WBUriValidator.MAX_PAGENAME_LENGHT + 1; i++)
	{
		name = name.concat("a");
	}
	wburi.setPageName(name);
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGENAME_LENGTH);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_pageName_toolong()
{
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	String name = "";
	for(int i = 0; i< WBUriValidator.MAX_PAGENAME_LENGHT + 1; i++)
	{
		name = name.concat("a");
	}
	wburi.setPageName(name);
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGENAME_LENGTH);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_uri_goodformat()
{
	wburi.setHttpOperation("GET");	
	String uri = "/test/aaa/bb-cc/aaa_123/123/AAA/~1";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_uri_badformat1()
{
	wburi.setHttpOperation("GET");	
	String uri = "/t?est/aaa/bb-cc/aaa_123/123/AAA/~1";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("uri", WBErrors.ERROR_URI_BAD_FORMAT);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_uri_badformat2()
{
	wburi.setHttpOperation("GET");	
	String uri = "/+?&";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("uri", WBErrors.ERROR_URI_BAD_FORMAT);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_uri_badformat1()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/t?est/aaa/bb-cc/aaa_123/123/AAA/~1";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("uri", WBErrors.ERROR_URI_BAD_FORMAT);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_uri_badformat2()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/+?&";
	wburi.setUri(uri);
	wburi.setPageName("test");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("uri", WBErrors.ERROR_URI_BAD_FORMAT);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateCreateWBUri_pageName_controller_badformat1()
{
	wburi.setHttpOperation("GET");	
	String uri = "/";
	wburi.setUri(uri);
	wburi.setPageName("?@$%");
	wburi.setControllerClass("?.%");
	Map<String, String> errors = uriValidator.validateCreate(wburi);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGE_BAD_FORMAT);
	errorsContainer.put("controllerClass", WBErrors.ERROR_CONTROLLER_BAD_FORMAT);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_pageName_controller_badformat2()
{
	wburi.setKey(10L);
	wburi.setHttpOperation("GET");	
	String uri = "/";
	wburi.setUri(uri);
	wburi.setPageName("?@$%");
	wburi.setControllerClass("?.%");
	Map<String, String> errors = uriValidator.validateUpdate(wburi);
	errorsContainer.put("pageName", WBErrors.ERROR_PAGE_BAD_FORMAT);
	errorsContainer.put("controllerClass", WBErrors.ERROR_CONTROLLER_BAD_FORMAT);
	assertTrue( errorsContainer.equals(errors));
}

@Test
public void test_validateUpdateWBUri_noKey()
{
	wburi.setHttpOperation("GET");	
	String uri = "/test";
	wburi.setUri(uri);
	Map<String, String> errors1 = uriValidator.validateUpdate(wburi);
	assertTrue( errors1.get("key").compareTo(WBErrors.ERROR_NO_KEY) == 0);

	wburi.setKey(0L);
	Map<String, String> errors2 = uriValidator.validateUpdate(wburi);
	assertTrue( errors2.get("key").compareTo(WBErrors.ERROR_NO_KEY) == 0);
}


}
