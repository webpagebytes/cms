package com.webpagebytes.cms.controllers;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.appinterfaces.WPBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.controllers.PageController;
import com.webpagebytes.cms.controllers.PageValidator;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

@Ignore
@RunWith(PowerMockRunner.class)
public class TestWBPageController {

private WPBWebPage objectForControllerMock;
private PageController controllerForTest;
private HttpServletRequest requestMock;
private HttpServletResponse responseMock;
private HttpServletToolbox httpServletToolboxMock;
private JSONToFromObjectConverter jsonObjectConverterMock;
private WPBAdminDataStorage adminStorageMock;
private PageValidator validatorMock;
private Map<String, String> errors;
private WPBWebPagesCache pageCacheMock;

@Before
public void setUp()
{
	objectForControllerMock = PowerMock.createMock(WPBWebPage.class);
	controllerForTest = new PageController();
	requestMock = PowerMock.createMock(HttpServletRequest.class);
	responseMock = PowerMock.createMock(HttpServletResponse.class);
	httpServletToolboxMock = PowerMock.createMock(HttpServletToolbox.class);
	jsonObjectConverterMock = PowerMock.createMock(JSONToFromObjectConverter.class);
	adminStorageMock = PowerMock.createMock(WPBAdminDataStorage.class);
	validatorMock = PowerMock.createMock(PageValidator.class);
	errors = new HashMap<String, String>();
	pageCacheMock = PowerMock.createMock(WPBWebPagesCache.class);
	
	controllerForTest.setAdminStorage(adminStorageMock);
	controllerForTest.setHttpServletToolbox(httpServletToolboxMock);
	controllerForTest.setJsonObjectConverter(jsonObjectConverterMock);
	controllerForTest.setPageValidator(validatorMock);
	controllerForTest.setPageCache(pageCacheMock);
}

@Test
public void test_create_ok()
{
	try
	{
		String externalKey = "abc123";
		String json = "{name:\"testpage\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		EasyMock.expect(adminStorageMock.getUniqueId()).andReturn(externalKey);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<String> captureExternalKey = new Capture<String>();
		Capture<Long> captureHash = new Capture<Long>();
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setExternalKey(EasyMock.capture(captureExternalKey));
		objectForControllerMock.setHash(EasyMock.captureLong(captureHash));
		EasyMock.expect(objectForControllerMock.getHtmlSource()).andReturn("");
		WPBWebPage newPage = new WPBWebPage();
		newPage.setPrivkey(10L);
		
		EasyMock.expect(adminStorageMock.add(objectForControllerMock)).andReturn(newPage);
		
		String returnJson = "{}"; //really doesn't matter
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(newPage, null)).andReturn(returnJson);
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.create(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue() == errors);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		assertTrue (captureDate.getValue() != null);
		assertTrue (captureExternalKey.getValue().equals(externalKey));
		assertTrue (captureHash.getValue().equals(0L));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_create_errors()
{
	try
	{
		String json = "{name:\"testpage\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		
		errors.put("uri", "error");
		
		String returnJson = "{}";
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.create(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue() == errors);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_create_exception()
{
	try
	{
		String externalKey = "abc123";
		String json = "{uri:\"test\", httpOperation:\"GET\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		EasyMock.expect(adminStorageMock.getUniqueId()).andReturn(externalKey);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<String> captureExternalKey = new Capture<String>();
		Capture<Long> captureHash = new Capture<Long>();
		
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setExternalKey(EasyMock.capture(captureExternalKey));
		objectForControllerMock.setHash(EasyMock.captureLong(captureHash));
		EasyMock.expect(objectForControllerMock.getHtmlSource()).andReturn("");		
		EasyMock.expect(adminStorageMock.add(objectForControllerMock)).andThrow(new WPBIOException(""));
		
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.create(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_CREATE_RECORD) == 0);
		assertTrue (captureData.getValue().equals("{}"));
		assertTrue (captureHttpResponse.getValue() == responseMock);
		assertTrue (captureDate.getValue() != null);
		assertTrue (captureExternalKey.getValue().equals(externalKey));
		assertTrue (captureHash.getValue().equals(0L));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getAll_ok()
{
	try
	{
		List<WPBWebPage> allUri = new ArrayList<WPBWebPage>();
		EasyMock.expect(adminStorageMock.getAllRecords(WPBWebPage.class)).andReturn(allUri);
		String jsonString = "{}";
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromListObjects(allUri)).andReturn(jsonString);
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
				   EasyMock.capture(captureData), 
				   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		controllerForTest.getAll(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);

		assertTrue (responseMock == captureHttpResponse.getValue());
		assertTrue (captureData.getValue().compareTo(jsonString) == 0);
		assertTrue (captureErrors.getValue() == null);
	} catch (WPBException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getAll_exception()
{
	try
	{
		List<Object> allUri = new ArrayList<Object>();
		EasyMock.expect(adminStorageMock.getAllRecords(WPBWebPage.class)).andThrow(new WPBIOException(""));
		String jsonString = "";
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
				   EasyMock.capture(captureData), 
				   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		controllerForTest.getAll(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (responseMock == captureHttpResponse.getValue());
		assertTrue (captureData.getValue().compareTo(jsonString) == 0);
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_GET_RECORDS) == 0);
	} catch (WPBException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_get_ok()
{
	try
	{
		String json = "{}";
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn("123");		
		EasyMock.expect(adminStorageMock.get(123L, WPBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(objectForControllerMock, null)).andReturn(json);
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.get(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureErrors.getValue() == null);
		assertTrue (captureData.getValue().compareTo(json) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_get_exception()
{
	try
	{
		String json = "{}";
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn("123");		
		EasyMock.expect(adminStorageMock.get(123L, WPBWebPage.class)).andThrow(new WPBIOException(""));

		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.get(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_GET_RECORDS) == 0);
		assertTrue (captureData.getValue().compareTo(json) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_get_noKey()
{
	try
	{
		String json = "{}";
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn(null);		
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.get(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_GET_RECORDS) == 0);
		assertTrue (captureData.getValue().compareTo(json) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_update_ok()
{
	try
	{
		String json = "{}";
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn("123");
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateUpdate(objectForControllerMock)).andReturn(errors);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<Long> captureKey = new Capture<Long>();
		Capture<Long> captureHash = new Capture<Long>();
		
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setPrivkey(EasyMock.captureLong(captureKey));
		objectForControllerMock.setHash(EasyMock.captureLong(captureHash));
		EasyMock.expect(objectForControllerMock.getHtmlSource()).andReturn("");
		WPBWebPage newPage = new WPBWebPage();
		newPage.setPrivkey(123L);
		EasyMock.expect(adminStorageMock.update(objectForControllerMock)).andReturn(newPage);
		
		String returnJson = "{}"; //really doesn't matter
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(newPage, null)).andReturn(returnJson);
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.update(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureErrors.getValue() == errors);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		assertTrue (captureKey.getValue().compareTo(123L) == 0);
		assertTrue (captureDate.getValue() != null);
		assertTrue (captureHash.getValue().equals(0L));
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_update_errors()
{
	try
	{
		String json = "{}";
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn("123");
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBWebPage.class)).andReturn(objectForControllerMock);
		Capture<Long> captureKey = new Capture<Long>();
		objectForControllerMock.setPrivkey(EasyMock.captureLong(captureKey));
		errors.put("uri", WPBErrors.ERROR_URI_LENGTH);
		EasyMock.expect(validatorMock.validateUpdate(objectForControllerMock)).andReturn(errors);
				
		String returnJson = "{}"; 
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.update(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureKey.getValue().compareTo(123L) == 0);
		assertTrue (captureErrors.getValue().get("uri").compareTo(WPBErrors.ERROR_URI_LENGTH) == 0);
		assertTrue (captureErrors.getValue().size() == 1);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_update_nokey()
{
	try
	{
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn(null);
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.update(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_UPDATE_RECORD) == 0);
		assertTrue (captureData.getValue().compareTo("{}") == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_delete_ok()
{
	try
	{
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn("123");
		
		Capture<Long> captureKey = new Capture<Long>();
		Capture<Class> captureClass = new Capture<Class>();
		adminStorageMock.delete(EasyMock.captureLong(captureKey), EasyMock.capture(captureClass));

		String returnJson = "{}"; //really doesn't matter
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(EasyMock.anyObject(WPBWebPage.class), EasyMock.anyObject(Map.class))).andReturn(returnJson);
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.delete(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue() == null);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);	
		assertTrue (captureKey.getValue().compareTo(123L) == 0);
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_delete_exception()
{
	try
	{
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn("123");
		
		Capture<Long> captureKey = new Capture<Long>();
		Capture<Class> captureClass = new Capture<Class>();
		adminStorageMock.delete(EasyMock.captureLong(captureKey), EasyMock.capture(captureClass));
		EasyMock.expectLastCall().andThrow(new WPBIOException(""));
		
		String returnJson = "{}"; //really doesn't matter
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.delete(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_DELETE_RECORD) == 0);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);	
		assertTrue (captureKey.getValue().compareTo(123L) == 0);
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_delete_noKey()
{
	try
	{
		Object key = EasyMock.expect(requestMock.getAttribute("key")).andReturn(null);
		
		String returnJson = "{}";
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.delete(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue().get("").compareTo(WPBErrors.WB_CANT_DELETE_RECORD) == 0);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);	
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_notify_ok()
{
	try
	{
	WPBWebPage pageMock = PowerMock.createMock(WPBWebPage.class);
	pageCacheMock.Refresh();
	EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, pageCacheMock, pageMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);	
	controllerForTest.notify(pageMock, WPBAdminDataStorageListener.AdminDataStorageOperation.CREATE_RECORD, WPBWebPage.class);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_notify_exception()
{
	try
	{
		WPBWebPage pageMock = PowerMock.createMock(WPBWebPage.class);
		pageCacheMock.Refresh();
		EasyMock.expectLastCall().andThrow(new WPBIOException(""));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, pageCacheMock, pageMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);	
		controllerForTest.notify(pageMock, WPBAdminDataStorageListener.AdminDataStorageOperation.CREATE_RECORD, WPBWebPage.class);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

}
