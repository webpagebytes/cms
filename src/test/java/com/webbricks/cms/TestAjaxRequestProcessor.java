package com.webbricks.cms;

import com.webbricks.exception.*;

import java.util.ArrayList;

import javax.servlet.http.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.easymock.EasyMock;
import org.easymock.Capture;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
public class TestAjaxRequestProcessor {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private AjaxRequestProcessor ajaxProcessor;
	
	@Before
	public void setUp()
	{
		ajaxProcessor = new AjaxRequestProcessor();		
		request = EasyMock.createMock(HttpServletRequest.class);
		response = EasyMock.createMock(HttpServletResponse.class);
	}
/*
	@Test
	public void testGetController()
	{
		try
		{
			DefaultController controller = (DefaultController) ajaxProcessor.getController("com.webbricks.cms.DefaultController");
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
			OperationsReader operationsReader = EasyMock.createMock(OperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webbricks.cms.TestDefaultController", "test"));
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webbricks.cms.TestDefaultController", "test"));
			
			ajaxProcessor.setOperationsReader(operationsReader);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			EasyMock.expect(request.getMethod()).andReturn("GET");
			Capture<String> contentType = new Capture<String>();
			response.setContentType( EasyMock.capture(contentType));
			EasyMock.replay(operationsReader, request, response);
			
			ajaxProcessor.process(request, response, reqUri);
			
			assertTrue (contentType.getValue().compareTo("application/json") == 0);
			TestDefaultController controller = (TestDefaultController) ajaxProcessor.getController("com.webbricks.cms.TestDefaultController");
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
			OperationsReader operationsReader = EasyMock.createMock(OperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webbricks.cms.TestDefaultController", "methodnotfound"));
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn(null);
			Capture<Integer> capture = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(capture));
		
			ajaxProcessor.setOperationsReader(operationsReader);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			EasyMock.expect(request.getMethod()).andReturn("GET");
			
			EasyMock.replay(operationsReader, request, response);
			
			ajaxProcessor.process(request, response, reqUri);
			
			assertTrue (capture.getValue() == HttpServletResponse.SC_NOT_FOUND);
		
		}catch (Exception e)
		{
			if (!(e instanceof WBException))
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
			OperationsReader operationsReader = EasyMock.createMock(OperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webbricks.cms.TestDefaultController", "methodnotfound"));
			EasyMock.expect(operationsReader.operationToMethod("/test", "GET")).andReturn( new Pair<String,String>("com.webbricks.cms.TestDefaultController", "methodnotfound"));
			
			ajaxProcessor.setOperationsReader(operationsReader);
			
			EasyMock.expect(request.getMethod()).andReturn("GET");
			EasyMock.expect(request.getMethod()).andReturn("GET");
			
			EasyMock.replay(operationsReader, request);
			
			ajaxProcessor.process(request, response, reqUri);
			
			assertTrue (false);
		
		}catch (Exception e)
		{
			if (!(e instanceof WBException))
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
			OperationsReader operationsReader = EasyMock.createMock(OperationsReader.class);
			EasyMock.expect(operationsReader.operationToMethod(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class))).andReturn(null);
			EasyMock.expect(request.getMethod()).andReturn("GET");
			ajaxProcessor.setOperationsReader(operationsReader);
			Capture<Integer> capture = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(capture));
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
			OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);
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
			OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);			
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
			OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);			
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
		
		OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);			
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
		
		OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
		EasyMock.expect(readerMock.operationToMethod("/uri/{key}", httpOperation)).andReturn(new Pair("",""));
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result.getFirst().compareTo("/uri/{key}") == 0);
		assertTrue(result.getSecond().compareTo("param1") == 0);				
	}

	@Test
	public void testmatchUrlForController_threeslashes()
	{
		String httpOperation = "POST";
		String httpUri = "/uri/param1/param2";
		
		OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(null);
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
		
		OperationsReader readerMock = EasyMock.createMock(OperationsReader.class);			
		EasyMock.expect(readerMock.operationToMethod(httpUri, httpOperation)).andReturn(new Pair("",""));
		EasyMock.replay(readerMock);
		ajaxProcessor.setOperationsReader(readerMock);			
		
		Pair<String,String> result = ajaxProcessor.matchUrlForController(httpUri, httpOperation);
		assertTrue(result.getFirst().compareTo("/uri") == 0);
		assertTrue(result.getSecond() == null);				
	}

}

