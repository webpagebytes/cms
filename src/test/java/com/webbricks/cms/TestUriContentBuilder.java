package com.webbricks.cms;

import static org.junit.Assert.*;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webbricks.appinterfaces.IWBRequestHandler;
import com.webbricks.appinterfaces.WBForward;
import com.webbricks.appinterfaces.WBModel;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.exception.WBException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UriContentBuilder.class})
public class TestUriContentBuilder {

WBCacheInstances cacheInstancesMock;
ModelBuilder modelBuilderMock;
UriContentBuilder uriContentBuilder;

@Before
public void setUp()
{
cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
uriContentBuilder = new UriContentBuilder(cacheInstancesMock, modelBuilderMock);

}

@Test
public void test_initialize()
{
	EasyMock.replay(cacheInstancesMock, modelBuilderMock);
	uriContentBuilder.initialize();
	EasyMock.verify(cacheInstancesMock, modelBuilderMock);
}

@Test
public void test_buildUriContent()
{
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	HttpServletRequest requestMock = EasyMock.createMock(HttpServletRequest.class);
	WBUri uriMock = EasyMock.createMock(WBUri.class);
	
	String controllerClass = "com.webbricks.cms.DummyRequestHandler";
	EasyMock.expect(uriMock.getControllerClass()).andReturn(controllerClass);
	
	WBModel model = new WBModel();
	WBForward forward = new WBForward();
	try
	{
	
		EasyMock.replay(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		uriContentBuilder.buildUriContent(requestMock, responseMock, uriMock, model, forward);
		EasyMock.verify(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		
		//verify that the controller is in customControllers map
		Map<String, IWBRequestHandler> controllers =  Whitebox.getInternalState(uriContentBuilder, "customControllers");
		assertTrue (controllers.get(controllerClass) != null);
		
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_buildUriContent_wrongController()
{
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	HttpServletRequest requestMock = EasyMock.createMock(HttpServletRequest.class);
	WBUri uriMock = EasyMock.createMock(WBUri.class);
	
	EasyMock.expect(uriMock.getControllerClass()).andReturn("com.webbricks.cms.DoesNotExist");
	
	WBModel model = new WBModel();
	WBForward forward = new WBForward();
	try
	{
	
		EasyMock.replay(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		uriContentBuilder.buildUriContent(requestMock, responseMock, uriMock, model, forward);
		EasyMock.verify(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		
	}catch (WBException e)
	{
		//OK
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildUriContent_controller_already_exists()
{
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	HttpServletRequest requestMock = EasyMock.createMock(HttpServletRequest.class);
	WBUri uriMock = EasyMock.createMock(WBUri.class);
	
	String controllerClass = "com.webbricks.cms.DummyRequestHandler";
	
	IWBRequestHandler controllerInst = null;
	try
	{
		controllerInst = (IWBRequestHandler) Class.forName(controllerClass).newInstance();
	} catch (Exception e)
	{
		assertTrue(false);
	}
	Map<String, IWBRequestHandler> controllers = Whitebox.getInternalState(uriContentBuilder, "customControllers");
	controllers.put(controllerClass, controllerInst);
	
	EasyMock.expect(uriMock.getControllerClass()).andReturn(controllerClass);
	
	WBModel model = new WBModel();
	WBForward forward = new WBForward();
	try
	{
	
		EasyMock.replay(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		uriContentBuilder.buildUriContent(requestMock, responseMock, uriMock, model, forward);
		EasyMock.verify(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		
		//verify that the controller is in customControllers map
		Map<String, IWBRequestHandler> controllers_ =  Whitebox.getInternalState(uriContentBuilder, "customControllers");
		assertTrue (controllers_.get(controllerClass) == controllerInst);
		assertTrue (controllers_.size() == 1);
		
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_buildUriContent_empty_controller()
{
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	HttpServletRequest requestMock = EasyMock.createMock(HttpServletRequest.class);
	WBUri uriMock = EasyMock.createMock(WBUri.class);
	
	String controllerClass = "";
	
	EasyMock.expect(uriMock.getControllerClass()).andReturn(controllerClass);	
	WBModel model = new WBModel();
	WBForward forward = new WBForward();
	try
	{
	
		EasyMock.replay(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		uriContentBuilder.buildUriContent(requestMock, responseMock, uriMock, model, forward);
		EasyMock.verify(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
				
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_buildUriContent_null_controller()
{
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	HttpServletRequest requestMock = EasyMock.createMock(HttpServletRequest.class);
	WBUri uriMock = EasyMock.createMock(WBUri.class);
	
	String controllerClass = null;
	
	EasyMock.expect(uriMock.getControllerClass()).andReturn(controllerClass);	
	WBModel model = new WBModel();
	WBForward forward = new WBForward();
	try
	{
	
		EasyMock.replay(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
		uriContentBuilder.buildUriContent(requestMock, responseMock, uriMock, model, forward);
		EasyMock.verify(responseMock, requestMock, uriMock, cacheInstancesMock, modelBuilderMock);
				
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

}
