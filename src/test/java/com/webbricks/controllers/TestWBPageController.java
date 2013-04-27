package com.webbricks.controllers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webbricks.cache.WBWebPageCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.controllers.WBErrors;
import com.webbricks.controllers.WBPageController;
import com.webbricks.controllers.WBPageValidator;
import com.webbricks.controllers.WBUriController;
import com.webbricks.controllers.WBUriValidator;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

@RunWith(PowerMockRunner.class)
public class TestWBPageController {

private WBWebPage objectForControllerMock;
private WBPageController controllerForTest;
private HttpServletRequest requestMock;
private HttpServletResponse responseMock;
private HttpServletToolbox httpServletToolboxMock;
private WBJSONToFromObjectConverter jsonObjectConverterMock;
private AdminDataStorage adminStorageMock;
private WBPageValidator validatorMock;
private Map<String, String> errors;
private WBWebPageCache pageCacheMock;

@Before
public void setUp()
{
	objectForControllerMock = PowerMock.createMock(WBWebPage.class);
	controllerForTest = new WBPageController();
	requestMock = PowerMock.createMock(HttpServletRequest.class);
	responseMock = PowerMock.createMock(HttpServletResponse.class);
	httpServletToolboxMock = PowerMock.createMock(HttpServletToolbox.class);
	jsonObjectConverterMock = PowerMock.createMock(WBJSONToFromObjectConverter.class);
	adminStorageMock = PowerMock.createMock(AdminDataStorage.class);
	validatorMock = PowerMock.createMock(WBPageValidator.class);
	errors = new HashMap<String, String>();
	pageCacheMock = PowerMock.createMock(WBWebPageCache.class);
	
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
		String json = "{name:\"testpage\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		EasyMock.expect(adminStorageMock.getUniqueId()).andReturn(1L);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<Long> captureExternalKey = new Capture<Long>();
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setExternalKey(EasyMock.captureLong(captureExternalKey));
		WBWebPage newPage = new WBWebPage();
		newPage.setKey(10L);
		
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
		assertTrue (captureExternalKey.getValue().equals(1L));
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
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WBWebPage.class)).andReturn(objectForControllerMock);
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
		String json = "{uri:\"test\", httpOperation:\"GET\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		EasyMock.expect(adminStorageMock.getUniqueId()).andReturn(2L);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<Long> captureExternalKey = new Capture<Long>();
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setExternalKey(EasyMock.captureLong(captureExternalKey));

		EasyMock.expect(adminStorageMock.add(objectForControllerMock)).andThrow(new WBIOException(""));
		
		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.create(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_CREATE_RECORD) == 0);
		assertTrue (captureData.getValue().equals("{}"));
		assertTrue (captureHttpResponse.getValue() == responseMock);
		assertTrue (captureDate.getValue() != null);
		assertTrue (captureExternalKey.getValue().equals(2L));
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
		List<Object> allUri = new ArrayList<Object>();
		EasyMock.expect(adminStorageMock.getAllRecords(WBWebPage.class)).andReturn(allUri);
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
	} catch (WBException e)
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
		EasyMock.expect(adminStorageMock.getAllRecords(WBWebPage.class)).andThrow(new WBIOException(""));
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
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_GET_RECORDS) == 0);
	} catch (WBException e)
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
		EasyMock.expect(adminStorageMock.get(123L, WBWebPage.class)).andReturn(objectForControllerMock);
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
		EasyMock.expect(adminStorageMock.get(123L, WBWebPage.class)).andThrow(new WBIOException(""));

		Capture<HttpServletResponse> captureHttpResponse = new Capture<HttpServletResponse>();
		Capture<String> captureData = new Capture<String>();
		Capture<Map<String, String>> captureErrors = new Capture<Map<String,String>>();
		httpServletToolboxMock.writeBodyResponseAsJson(EasyMock.capture(captureHttpResponse), 
												   EasyMock.capture(captureData), 
												   EasyMock.capture(captureErrors));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		controllerForTest.get(requestMock, responseMock, "/abc");
		EasyMock.verify(httpServletToolboxMock, requestMock, responseMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);
		
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_GET_RECORDS) == 0);
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
		
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_GET_RECORDS) == 0);
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
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WBWebPage.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateUpdate(objectForControllerMock)).andReturn(errors);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<Long> captureKey = new Capture<Long>();
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setKey(EasyMock.captureLong(captureKey));

		WBWebPage newPage = new WBWebPage();
		newPage.setKey(123L);
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
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WBWebPage.class)).andReturn(objectForControllerMock);
		Capture<Long> captureKey = new Capture<Long>();
		objectForControllerMock.setKey(EasyMock.captureLong(captureKey));
		errors.put("uri", WBErrors.ERROR_URI_LENGTH);
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
		assertTrue (captureErrors.getValue().get("uri").compareTo(WBErrors.ERROR_URI_LENGTH) == 0);
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
		
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_UPDATE_RECORD) == 0);
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
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(EasyMock.anyObject(WBWebPage.class), EasyMock.anyObject(Map.class))).andReturn(returnJson);
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
		EasyMock.expectLastCall().andThrow(new WBIOException(""));
		
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
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_DELETE_RECORD) == 0);
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
		assertTrue (captureErrors.getValue().get("").compareTo(WBErrors.WB_CANT_DELETE_RECORD) == 0);
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
	WBWebPage pageMock = PowerMock.createMock(WBWebPage.class);
	pageCacheMock.Refresh();
	EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, pageCacheMock, pageMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);	
	controllerForTest.notify(pageMock, AdminDataStorageListener.AdminDataStorageOperation.CREATE);
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
		WBWebPage pageMock = PowerMock.createMock(WBWebPage.class);
		pageCacheMock.Refresh();
		EasyMock.expectLastCall().andThrow(new WBIOException(""));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, pageCacheMock, pageMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);	
		controllerForTest.notify(pageMock, AdminDataStorageListener.AdminDataStorageOperation.CREATE);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

}
