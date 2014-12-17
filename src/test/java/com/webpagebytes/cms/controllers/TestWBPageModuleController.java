package com.webpagebytes.cms.controllers;
import org.junit.runner.RunWith;

import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import org.powermock.api.easymock.PowerMock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.appinterfaces.WPBPageModulesCache;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.controllers.PageModuleController;
import com.webpagebytes.cms.controllers.PageModuleValidator;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

@Ignore
@RunWith(PowerMockRunner.class)
public class TestWBPageModuleController {

private WPBPageModule objectForControllerMock;
private PageModuleController controllerForTest;
private HttpServletRequest requestMock;
private HttpServletResponse responseMock;
private HttpServletToolbox httpServletToolboxMock;
private JSONToFromObjectConverter jsonObjectConverterMock;
private WPBAdminDataStorage adminStorageMock;
private PageModuleValidator validatorMock;
private Map<String, String> errors;
private WPBPageModulesCache pageModuleCacheMock;

@Before
public void setUp()
{
	objectForControllerMock = PowerMock.createMock(WPBPageModule.class);
	controllerForTest = new PageModuleController();
	requestMock = PowerMock.createMock(HttpServletRequest.class);
	responseMock = PowerMock.createMock(HttpServletResponse.class);
	httpServletToolboxMock = PowerMock.createMock(HttpServletToolbox.class);
	jsonObjectConverterMock = PowerMock.createMock(JSONToFromObjectConverter.class);
	adminStorageMock = PowerMock.createMock(WPBAdminDataStorage.class);
	validatorMock = PowerMock.createMock(PageModuleValidator.class);
	errors = new HashMap<String, String>();
	pageModuleCacheMock = PowerMock.createMock(WPBPageModulesCache.class);
	
	controllerForTest.setAdminStorage(adminStorageMock);
	controllerForTest.setHttpServletToolbox(httpServletToolboxMock);
	controllerForTest.setJsonObjectConverter(jsonObjectConverterMock);
	controllerForTest.setValidator(validatorMock);
	controllerForTest.setPageModuleCache(pageModuleCacheMock);	
}


@Test
public void test_create_ok()
{
	try
	{
		String externalKey="123abc";
		String json = "{name:\"testpage\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBPageModule.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		EasyMock.expect(adminStorageMock.getUniqueId()).andReturn(externalKey);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<String> captureExternalKey = new Capture<String>();
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setExternalKey(EasyMock.capture(captureExternalKey));
		WPBPageModule newPageModule = new WPBPageModule();
		newPageModule.setPrivkey(10L);
		EasyMock.expect(adminStorageMock.add(objectForControllerMock)).andReturn(newPageModule);
		
		String returnJson = "{}"; //really doesn't matter
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(newPageModule, null)).andReturn(returnJson);
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
		assertTrue (captureExternalKey.getValue().compareTo(externalKey) == 0);
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
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBPageModule.class)).andReturn(objectForControllerMock);
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
		String externalKey = "123abc";
		String json = "{uri:\"test\", httpOperation:\"GET\"}";
		EasyMock.expect(httpServletToolboxMock.getBodyText(requestMock)).andReturn(json);
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBPageModule.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateCreate(objectForControllerMock)).andReturn(errors);
		EasyMock.expect(adminStorageMock.getUniqueId()).andReturn(externalKey);
		Capture<Date> captureDate = new Capture<Date>();
		Capture<String> captureExternalKey = new Capture<String>();
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		objectForControllerMock.setExternalKey(EasyMock.capture(captureExternalKey));

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
		assertTrue (captureExternalKey.getValue().compareTo(externalKey)==0);
		
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
		List<WPBPageModule> all = new ArrayList<WPBPageModule>();
		EasyMock.expect(adminStorageMock.getAllRecords(WPBPageModule.class)).andReturn(all);
		String jsonString = "{}";
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromListObjects(all)).andReturn(jsonString);
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
		EasyMock.expect(adminStorageMock.getAllRecords(WPBPageModule.class)).andThrow(new WPBIOException(""));
		String jsonString = "{}";
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
		EasyMock.expect(adminStorageMock.get(123L, WPBPageModule.class)).andReturn(objectForControllerMock);
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
		EasyMock.expect(adminStorageMock.get(123L, WPBPageModule.class)).andThrow(new WPBIOException(""));

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
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBPageModule.class)).andReturn(objectForControllerMock);
		EasyMock.expect(validatorMock.validateUpdate(objectForControllerMock)).andReturn(errors);
		Capture<Long> captureKey = new Capture<Long>();
		Capture<Date> captureDate = new Capture<Date>();
		objectForControllerMock.setPrivkey(EasyMock.captureLong(captureKey));
		objectForControllerMock.setLastModified(EasyMock.capture(captureDate));
		WPBPageModule newPageModule = new WPBPageModule();
		newPageModule.setPrivkey(123L);
		EasyMock.expect(adminStorageMock.update(objectForControllerMock)).andReturn(newPageModule);
		
		String returnJson = "{}"; //really doesn't matter
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(newPageModule, null)).andReturn(returnJson);
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
		EasyMock.expect(jsonObjectConverterMock.objectFromJSONString(json, WPBPageModule.class)).andReturn(objectForControllerMock);
		Capture<Long> captureKey = new Capture<Long>();
		objectForControllerMock.setPrivkey(EasyMock.captureLong(captureKey));

		errors.put("uri", WPBErrors.ERROR_URI_START_CHAR);
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
		
		assertTrue (captureErrors.getValue().get("uri").compareTo(WPBErrors.ERROR_URI_START_CHAR) == 0);
		assertTrue (captureErrors.getValue().size() == 1);
		assertTrue (captureData.getValue().compareTo(returnJson) == 0);
		assertTrue (captureHttpResponse.getValue() == responseMock);
		assertTrue (captureKey.getValue().compareTo(123L) == 0);
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
		EasyMock.expect(jsonObjectConverterMock.JSONStringFromObject(EasyMock.anyObject(WPBPageModule.class), EasyMock.anyObject(Map.class))).andReturn(returnJson);
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
	WPBPageModule pageModuleMock = PowerMock.createMock(WPBPageModule.class);
	pageModuleCacheMock.Refresh();
	EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, pageModuleCacheMock, pageModuleMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);	
	controllerForTest.notify(pageModuleMock, WPBAdminDataStorageListener.AdminDataStorageOperation.CREATE_RECORD, WPBPageModule.class);
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
		WPBPageModule pageMock = PowerMock.createMock(WPBPageModule.class);
		pageModuleCacheMock.Refresh();
		EasyMock.expectLastCall().andThrow(new WPBIOException(""));
		EasyMock.replay(httpServletToolboxMock, requestMock, responseMock, pageModuleCacheMock, pageMock, jsonObjectConverterMock, validatorMock, adminStorageMock, objectForControllerMock);	
		controllerForTest.notify(pageMock, WPBAdminDataStorageListener.AdminDataStorageOperation.CREATE_RECORD, WPBPageModule.class);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

}

