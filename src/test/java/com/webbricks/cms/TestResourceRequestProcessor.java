package com.webbricks.cms;

import com.webbricks.exception.WBException;


import com.webbricks.exception.WBResourceNotFoundException;

import javax.servlet.ServletOutputStream;
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

public class TestResourceRequestProcessor{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private StaticResourceMap resourceMap;
	
	@Before
	public void setUp()
	{
		request = EasyMock.createMock(HttpServletRequest.class);
		response = EasyMock.createMock(HttpServletResponse.class);
		resourceMap = EasyMock.createMock(StaticResourceMap.class);
	}
	
	@Test
	public void testIsResourceRequest()
	{
		ResourceRequestProcessor resourceRequestProcessor = new ResourceRequestProcessor();
		String uri = "/admin/js/test.js";
		resourceRequestProcessor.isResourceRequest(uri);
		uri = "/admin/css/test.css";
		assertTrue(resourceRequestProcessor.isResourceRequest(uri));
		uri = "/admin/img/test.png";
		assertTrue(resourceRequestProcessor.isResourceRequest(uri));
		uri = "/admin/img/test.jpg";
		assertTrue(resourceRequestProcessor.isResourceRequest(uri));
		uri = "/admin/test.html";
		assertTrue(resourceRequestProcessor.isResourceRequest(uri));
		uri = "/admin/test";
		assertTrue(false == resourceRequestProcessor.isResourceRequest(uri));
		uri = "test";
		assertTrue(false == resourceRequestProcessor.isResourceRequest(uri));
		uri = "/";
		assertTrue(false == resourceRequestProcessor.isResourceRequest(uri));
		uri = "/xxx.";
		assertTrue(false == resourceRequestProcessor.isResourceRequest(uri));
	}
	@Test
	public void testAddContentType_notAFile()
	{
		EasyMock.replay(response);
		ResourceRequestProcessor processor = new ResourceRequestProcessor();
		
		assertTrue(null == processor.addContentType(response, "/path"));
		EasyMock.verify(response);
	}

	@Test
	public void testAddContentType_notAKnownResource()
	{
		EasyMock.replay(request);
		ResourceRequestProcessor processor = new ResourceRequestProcessor();
		
		assertTrue(null == processor.addContentType(response, "/path.svi"));
		EasyMock.verify(request);
	}

	@Test
	public void testInitOK()
	{
		ResourceRequestProcessor processor = new ResourceRequestProcessor();
		Throwable exception = null;
		try
		{
			processor.initialize("admin", "META-INF/config/resourceswhitelist.properties");
		} catch (WBException e)
		{
			exception = e;
		}
		assertTrue (exception == null);
	}
		
	@Test
	public void testProcessRequestOK_cache()
	{
		// the request of a resource that returns 304
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/123/base.css";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(resourceMap.getResource("/base.css")).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureExpireValue = new Capture<String>();
			Capture<String> captureExpire = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureExpire), EasyMock.capture(captureExpireValue));
			response.setContentType(EasyMock.capture(captureContentType));
			Capture<byte[]> captureContent = new Capture<byte[]>();
			os.write(EasyMock.capture(captureContent));
			
			EasyMock.replay(response);
			EasyMock.replay(resourceMap);
			EasyMock.replay(request);	
			EasyMock.replay(os);
			
			processor.process(request, response, resource);
			EasyMock.verify(response);
			EasyMock.verify(request);
			assertTrue (captureContentType.getValue().compareTo("text/css") == 0);
			assertTrue (captureExpire.getValue().compareTo("cache-control") == 0);
			assertTrue (captureExpireValue.getValue().compareTo("max-age=86400") == 0);
			
			assertTrue (java.util.Arrays.equals(captureContent.getValue(), content.getBytes()));
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testProcessRequestOK_nocache()
	{
		// the request of a resource that returns 304
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/123/base.html";
			String content = "<html>1234</html>";
			
			EasyMock.expect(resourceMap.getResource("/base.html")).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureExpireValue = new Capture<String>();
			Capture<String> captureExpire = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureExpire), EasyMock.capture(captureExpireValue));
			response.setContentType(EasyMock.capture(captureContentType));
			Capture<byte[]> captureContent = new Capture<byte[]>();
			os.write(EasyMock.capture(captureContent));
			
			EasyMock.replay(response);
			EasyMock.replay(resourceMap);
			EasyMock.replay(request);	
			EasyMock.replay(os);
			
			processor.process(request, response, resource);
			EasyMock.verify(response);
			EasyMock.verify(request);
			assertTrue (captureContentType.getValue().compareTo("text/html") == 0);
			assertTrue (captureExpire.getValue().compareTo("cache-control") == 0);
			assertTrue (captureExpireValue.getValue().compareTo("no-cache;no-store;") == 0);
			
			assertTrue (java.util.Arrays.equals(captureContent.getValue(), content.getBytes()));
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}


	@Test
	public void testProcessRequestNotFoundException()
	{
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/123/notfound.css";
			String hash = "1234";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(resourceMap.getResource("/notfound.css")).andThrow(new WBResourceNotFoundException("resource not found in testing"));
			
			Capture<Integer> captureCode = new Capture<Integer>();
			response.sendError(EasyMock.captureInt(captureCode));
			
			EasyMock.replay(response);
			EasyMock.replay(resourceMap);
			EasyMock.replay(request);	
			
			processor.process(request, response, resource);
			EasyMock.verify(response);
			EasyMock.verify(request);
			assertTrue (captureCode.getValue().compareTo( HttpServletResponse.SC_NOT_FOUND) == 0);
		}
		catch (Exception e)
		{
			assertTrue (false);
		}
	}
	
	@Test
	public void testGetResourceMap()
	{
		ResourceRequestProcessor processor = new ResourceRequestProcessor();
		assertTrue(processor.getResourcesMap() != null);
	}

}
