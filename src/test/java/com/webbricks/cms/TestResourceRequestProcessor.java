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
		
		assertTrue(false == processor.addContentType(response, "/path"));
		EasyMock.verify(response);
	}

	@Test
	public void testAddContentType_notAKnownResource()
	{
		EasyMock.replay(request);
		ResourceRequestProcessor processor = new ResourceRequestProcessor();
		
		assertTrue(false == processor.addContentType(response, "/path.svg"));
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
	public void testProcessRequestNotModified()
	{
		// the request of a resource that returns 304
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/image.png";
			String hash = "1234";
			EasyMock.expect(resourceMap.getResourceHash(resource)).andReturn(hash);
			EasyMock.expect(request.getHeader("If-None-Match")).andReturn(hash);
			Capture<Integer> capture = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(capture));	
			
			Capture<String> captureEtag = new Capture<String>();
			Capture<String> captureEtagValue = new Capture<String>();
			response.addHeader(EasyMock.capture(captureEtag), EasyMock.capture(captureEtagValue));
			EasyMock.replay(response);
			EasyMock.replay(resourceMap);
			EasyMock.replay(request);	
			
			processor.process(request, response, resource);
			EasyMock.verify(response);
			EasyMock.verify(request);
			assertTrue (capture.getValue() == HttpServletResponse.SC_NOT_MODIFIED);
			assertTrue (captureEtag.getValue().compareTo("Etag") == 0);
			assertTrue (captureEtagValue.getValue().compareTo(hash) == 0);
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}
	
	@Test
	public void testProcessRequestOK()
	{
		// the request of a resource that returns 304
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/base.css";
			String hash = "1234";
			String differentHash = "9999";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(request.getHeader("If-None-Match")).andReturn(differentHash);
			EasyMock.expect(resourceMap.getResourceHash(resource)).andReturn(hash);
			EasyMock.expect(resourceMap.getResource(resource)).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureEtag = new Capture<String>();
			Capture<String> captureEtagValue = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureEtag), EasyMock.capture(captureEtagValue));
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
			assertTrue (captureEtag.getValue().compareTo("Etag") == 0);
			assertTrue (captureEtagValue.getValue().compareTo(hash) == 0);
			
			assertTrue (java.util.Arrays.equals(captureContent.getValue(), content.getBytes()));
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testProcessRequest_noIfNotModified()
	{
		// the request of a resource that returns 304
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/base.css";
			String hash = "1234";
			String differentHash = "9999";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(request.getHeader("If-None-Match")).andReturn(null);
			EasyMock.expect(resourceMap.getResourceHash(resource)).andReturn(hash);
			EasyMock.expect(resourceMap.getResource(resource)).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureEtag = new Capture<String>();
			Capture<String> captureEtagValue = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureEtag), EasyMock.capture(captureEtagValue));
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
			assertTrue (captureEtag.getValue().compareTo("Etag") == 0);
			assertTrue (captureEtagValue.getValue().compareTo(hash) == 0);
			
			assertTrue (java.util.Arrays.equals(captureContent.getValue(), content.getBytes()));
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testProcessRequest_wrongFileExtension()
	{
		// the request of a resource that returns 304
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMap);
		
			String resource = "/base.svg";
			String hash = "1234";
			String differentHash = "9999";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(request.getHeader("If-None-Match")).andReturn(null);
			EasyMock.expect(resourceMap.getResourceHash(resource)).andReturn(hash);
			EasyMock.expect(resourceMap.getResource(resource)).andReturn(content.getBytes());
			
			Capture<String> captureEtag = new Capture<String>();
			Capture<String> captureEtagValue = new Capture<String>();
			response.addHeader(EasyMock.capture(captureEtag), EasyMock.capture(captureEtagValue));
			
			EasyMock.replay(response);
			EasyMock.replay(resourceMap);
			EasyMock.replay(request);	
			
			processor.process(request, response, resource);
			EasyMock.verify(response);
			EasyMock.verify(request);
			assertTrue (captureEtag.getValue().compareTo("Etag") == 0);
			assertTrue (captureEtagValue.getValue().compareTo(hash) == 0);
			
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
		
			String resource = "/notfound.css";
			String hash = "1234";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(request.getHeader("If-None-Match")).andReturn(hash);
			EasyMock.expect(resourceMap.getResourceHash(resource)).andThrow(new WBResourceNotFoundException("resource not found in testing"));
			
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
