package com.webpagebytes.cms.engine;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.engine.AdminServletOperationsReader;
import com.webpagebytes.cms.engine.AjaxRequestProcessor;
import com.webpagebytes.cms.exception.*;
import com.webpagebytes.cms.utility.Pair;

import javax.servlet.http.*;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.easymock.EasyMock;
import org.easymock.Capture;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class TestAjaxRequestProcessor {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private AjaxRequestProcessor ajaxProcessor;
	
	WPBFileStorage fileStorageMock;
	WPBAdminDataStorage adminDataStorageMock;
	@Before
	public void setUp()
	{
		ajaxProcessor = new AjaxRequestProcessor();		
		request = EasyMock.createMock(HttpServletRequest.class);
		response = EasyMock.createMock(HttpServletResponse.class);
		
		fileStorageMock = EasyMock.createMock(WPBFileStorage.class);
		adminDataStorageMock = EasyMock.createMock(WPBInternalAdminDataStorage.class);
		
		Whitebox.setInternalState(WPBFileStorageFactory.class, "instance", fileStorageMock);
		Whitebox.setInternalState(WPBAdminDataStorageFactory.class, "instance", adminDataStorageMock);
        
	}

	@After
	public void tearDown()
	{
	    Whitebox.setInternalState(WPBFileStorageFactory.class, "instance", (WPBFileStorage)null);
        Whitebox.setInternalState(WPBAdminDataStorageFactory.class, "instance", (WPBInternalAdminDataStorage)null);    
	}
/*
	@Test
	public void testGetController()
	{
		try
		{
			DefaultController controller = (DefaultController) ajaxProcessor.getController("com.webpagebytes.cms.DefaultController");
			assertTrue(controller != null);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testGetController_notFound()
	{
		try
		{
			DefaultController controller = (DefaultController) ajaxProcessor.getController("ClassNotExistent");
			assertTrue(false);
		} catch (Exception e)
		{
			if (!(e instanceof WBException))
			{
				assertTrue(false);
			}
		}
	}
*/
	@Test
	public void testProcessRequest()
	{
		try
		{
			String reqUri = "/test";
			AdminServletOperationsReader operationsReader = EasyMock.createMock(AdminServletOperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webpagebytes.cms.engine.TestDefaultController", "test"));
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webpagebytes.cms.engine.TestDefaultController", "test"));
			
			ajaxProcessor.setOperationsReader(operationsReader);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			EasyMock.expect(request.getMethod()).andReturn("GET");
			Capture<String> contentType = new Capture<String>();
			response.setContentType( EasyMock.capture(contentType));
			
			response.addHeader(AjaxRequestProcessor.PRAGMA_HEADER, AjaxRequestProcessor.NO_CACHE_HEADER);
			response.addHeader(AjaxRequestProcessor.CACHE_CONTROL_HEADER, AjaxRequestProcessor.NO_CACHE_HEADER);

			EasyMock.replay(operationsReader, request, response);
			
			ajaxProcessor.process(request, response, reqUri);
			
			assertTrue (contentType.getValue().compareTo("application/json") == 0);
			TestDefaultController controller = (TestDefaultController) ajaxProcessor.getController("com.webpagebytes.cms.engine.TestDefaultController");
			assertTrue(controller.getUriValue().compareTo("/test") == 0);
		
		}catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testProcessRequest_controllerNotFound()
	{
		try
		{
			String reqUri = "/test";
			AdminServletOperationsReader operationsReader = EasyMock.createMock(AdminServletOperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webpagebytes.cms.TestDefaultController", "methodnotfound"));
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn(null);
			Capture<Integer> capture = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(capture));
		
			ajaxProcessor.setOperationsReader(operationsReader);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			EasyMock.expect(request.getMethod()).andReturn("GET");
			response.addHeader(AjaxRequestProcessor.PRAGMA_HEADER, AjaxRequestProcessor.NO_CACHE_HEADER);
			response.addHeader(AjaxRequestProcessor.CACHE_CONTROL_HEADER, AjaxRequestProcessor.NO_CACHE_HEADER);
	
			EasyMock.replay(operationsReader, request, response);
			
			ajaxProcessor.process(request, response, reqUri);
			
			assertTrue (capture.getValue() == HttpServletResponse.SC_NOT_FOUND);
		
		}catch (Exception e)
		{
			if (!(e instanceof WPBException))
			{
				assertTrue (false);
			}
		}
	}

	@Test
	public void testProcessRequest_controllerMethodNotFound()
	{
		try
		{
			String reqUri = "/test";
			AdminServletOperationsReader operationsReader = EasyMock.createMock(AdminServletOperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webpagebytes.cms.TestDefaultController", "methodnotfound"));
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webpagebytes.cms.TestDefaultController", "methodnotfound"));
			
			ajaxProcessor.setOperationsReader(operationsReader);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			EasyMock.expect(request.getMethod()).andReturn("GET");
			
			EasyMock.replay(operationsReader, request);
			
			ajaxProcessor.process(request, response, reqUri);
			
			assertTrue (false);
		
		}catch (Exception e)
		{
			if (!(e instanceof WPBException))
			{
				assertTrue (false);
			}
		}
	}

	@Test
	public void testProcessRequest_notFound()
	{
		try
		{
			AdminServletOperationsReader operationsReader = EasyMock.createMock(AdminServletOperationsReader.class);
			
			EasyMock.expect(operationsReader.operationToMethod(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(null);
			EasyMock.expect(operationsReader.wildOperationToMethod(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(null);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			ajaxProcessor.setOperationsReader(operationsReader);
			Capture<Integer> capture = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(capture));
			
			response.addHeader(AjaxRequestProcessor.PRAGMA_HEADER, AjaxRequestProcessor.NO_CACHE_HEADER);
			response.addHeader(AjaxRequestProcessor.CACHE_CONTROL_HEADER, AjaxRequestProcessor.NO_CACHE_HEADER);

			EasyMock.replay(operationsReader, request, response);			
			ajaxProcessor.process(request, response, "/test");
			
			assertTrue (capture.getValue() == HttpServletResponse.SC_NOT_FOUND);
			
		} catch(Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testInitialize()
	{
		try
		{
			String path = "META-INF/config/ajaxwhitelist.properties";
			AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);
			readerMock.initialize(path);
			
			ajaxProcessor.setOperationsReader(readerMock);
			EasyMock.replay(readerMock);
			ajaxProcessor.initialize("config", "ajaxwhitelist.properties");
			EasyMock.verify(readerMock);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testIsAjaxRequest()
	{
		try
		{
			String httpOperation = "POST";
			String httpUri = "/uri/param1";
			
			HttpServletRequest req = EasyMock.createMock(HttpServletRequest.class);
			AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
			EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
			EasyMock.expect(readerMock.operationToMethod("/uri/{key}", httpOperation)).andReturn(new Pair("",""));
			EasyMock.expect(req.getMethod()).andReturn(httpOperation);
			EasyMock.replay(readerMock, req);
			ajaxProcessor.setOperationsReader(readerMock);			
			
			boolean result = ajaxProcessor.isAjaxRequest(req, httpUri);
			assertTrue(result == true);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testIsAjaxRequest_false()
	{
		try
		{
			String httpOperation = "POST";
			String httpUri = "/uri/param1";
			
			HttpServletRequest req = EasyMock.createMock(HttpServletRequest.class);
			AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
			EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
			EasyMock.expect(readerMock.operationToMethod("/uri/{key}", httpOperation)).andReturn(null);
			EasyMock.expect(req.getMethod()).andReturn(httpOperation);
			EasyMock.replay(readerMock, req);
			ajaxProcessor.setOperationsReader(readerMock);			
			
			boolean result = ajaxProcessor.isAjaxRequest(req, httpUri);
			assertTrue(result == false);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testGetControllersMap()
	{
		assertTrue (ajaxProcessor.getControllersMap() != null);
	}
	
	@Test
	public void testmatchUrlForController_keyint()
	{
		String httpOperation = "POST";
		String httpUri = "/uri/12367";
		
		AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
		EasyMock.expect(readerMock.operationToMethod("/uri/{key}", httpOperation)).andReturn(new Pair("",""));
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result.getFirst().compareTo("/uri/{key}") == 0);
		assertTrue(result.getSecond().compareTo("12367") == 0);
				
	}
	
	@Test
	public void testmatchUrlForController_keystring()
	{
		String httpOperation = "POST";
		String httpUri = "/uri/param1";
		
		AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
		EasyMock.expect(readerMock.operationToMethod("/uri/{key}", httpOperation)).andReturn(new Pair("",""));
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result.getFirst().compareTo("/uri/{key}") == 0);
		assertTrue(result.getSecond().compareTo("param1") == 0);				
	}

	@Test
	public void testmatchUrlForController_three_keystring()
	{
		String httpOperation = "POST";
		String httpUri = "/uri/ext/param1";
		
		AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
		EasyMock.expect(readerMock.operationToMethod("/uri/ext/{key}", httpOperation)).andReturn(new Pair("",""));
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result.getFirst().compareTo("/uri/ext/{key}") == 0);
		assertTrue(result.getSecond().compareTo("param1") == 0);				
	}

	@Test
	public void testmatchUrlForController_threeslashes()
	{
		String httpOperation = "POST";
		String httpUri = "/uri/param1/param2";
		
		AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
		EasyMock.expect(readerMock.operationToMethod("/uri/param1/{key}", httpOperation)).andReturn(null);
		
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result == null);				
	}

	@Test
	public void testmatchUrlForController_defaultUri()
	{
		String httpOperation = "GET";
		String httpUri = "/uri";
		
		AdminServletOperationsReader readerMock = EasyMock.createMock(AdminServletOperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(new Pair("",""));
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result.getFirst().compareTo("/uri") == 0);
		assertTrue(result.getSecond() == null);				
	}

}

