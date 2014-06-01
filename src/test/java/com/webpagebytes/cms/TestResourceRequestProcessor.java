package com.webpagebytes.cms;

import java.io.IOException;
import com.webpagebytes.cms.ResourceRequestProcessor;
import com.webpagebytes.cms.StaticResourceMap;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.exception.WBResourceNotFoundException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import org.junit.Test;
import org.junit.Before;
import org.easymock.EasyMock;
import org.easymock.Capture;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestResourceRequestProcessor{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private StaticResourceMap resourceMapMock;
	
	@Before
	public void setUp()
	{
		request = EasyMock.createMock(HttpServletRequest.class);
		response = EasyMock.createMock(HttpServletResponse.class);
		resourceMapMock = EasyMock.createMock(StaticResourceMap.class);
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
	public void testProcessRequest_WrongContentType()
	{
		try
		{
			String resourceFullPath = "/123/base.xml", resourceBasePath = "/base.xml";
			
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMapMock);
		
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(resourceMapMock.getResource(resourceBasePath)).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			Capture<Integer> captureStatus = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(captureStatus));
			EasyMock.replay(response, resourceMapMock, request, os);
			
			processor.process(request, response, resourceFullPath);
			
			EasyMock.verify(response, resourceMapMock, request, os);
			
			assertTrue (captureStatus.getValue() == HttpServletResponse.SC_NOT_FOUND);
			
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	public void testProcessRequest_parameters(String resourceFullPath, String resourceBasePath)
	{
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMapMock);
		
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(resourceMapMock.getResource(resourceBasePath)).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureExpireValue = new Capture<String>();
			Capture<String> captureExpire = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureExpire), EasyMock.capture(captureExpireValue));
			response.setContentType(EasyMock.capture(captureContentType));
			Capture<byte[]> captureContent = new Capture<byte[]>();
			os.write(EasyMock.capture(captureContent));
			
			EasyMock.replay(response, resourceMapMock, request, os);
			
			processor.process(request, response, resourceFullPath);
			EasyMock.verify(response, resourceMapMock, request, os);
			
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
	public void testProcessRequestOK_beginsWithSlash()
	{
		testProcessRequest_parameters("/123/base.css", "/base.css");
	}

	@Test
	public void testProcessRequestOK_doesNotContainMultipleSlash()
	{
		testProcessRequest_parameters("/base.css", "/base.css");
	}

	@Test
	public void testProcessRequestOK_doesNotBeginWithSlash()
	{
		testProcessRequest_parameters("base.css", "base.css");
	}

	@Test
	public void testProcessRequestOK_nocache()
	{
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMapMock);
		
			String resource = "/123/base.html";
			String content = "<html>1234</html>";
			
			EasyMock.expect(resourceMapMock.getResource("/base.html")).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureExpireValue = new Capture<String>();
			Capture<String> captureExpire = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureExpire), EasyMock.capture(captureExpireValue));
			response.setContentType(EasyMock.capture(captureContentType));
			Capture<byte[]> captureContent = new Capture<byte[]>();
			os.write(EasyMock.capture(captureContent));
			
			EasyMock.replay(response, resourceMapMock, request, os);			
			
			processor.process(request, response, resource);
			EasyMock.verify(response, resourceMapMock, request, os);
			
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
	public void testProcessRequest_IOExcepion()
	{
		try
		{
			ResourceRequestProcessor processor = new ResourceRequestProcessor();
			processor.setResourcesMap(resourceMapMock);
		
			String resource = "/123/base.html";
			String content = "<html>1234</html>";
			
			EasyMock.expect(resourceMapMock.getResource("/base.html")).andReturn(content.getBytes());
			ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
			EasyMock.expect(response.getOutputStream()).andReturn(os);
			
			Capture<String> captureExpireValue = new Capture<String>();
			Capture<String> captureExpire = new Capture<String>();
			Capture<String> captureContentType = new Capture<String>();
			response.addHeader(EasyMock.capture(captureExpire), EasyMock.capture(captureExpireValue));
			response.setContentType(EasyMock.capture(captureContentType));
			Capture<byte[]> captureContent = new Capture<byte[]>();
			os.write(EasyMock.capture(captureContent));
			EasyMock.expectLastCall().andThrow(new IOException());
			
			EasyMock.replay(response, resourceMapMock, request, os);			
			
			processor.process(request, response, resource);
			EasyMock.verify(response, resourceMapMock, request, os);			
			assertTrue (captureContentType.getValue().compareTo("text/html") == 0);
			assertTrue (captureExpire.getValue().compareTo("cache-control") == 0);
			assertTrue (captureExpireValue.getValue().compareTo("no-cache;no-store;") == 0);
			
		} 
		catch (WBIOException e)
		{
			// OK
		}
		catch (Exception e)
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
			processor.setResourcesMap(resourceMapMock);
		
			String resource = "/123/notfound.css";
			String hash = "1234";
			String content = " body { color: #123456; } \n a { color: #567890; }";
			
			EasyMock.expect(resourceMapMock.getResource("/notfound.css")).andThrow(new WBResourceNotFoundException("resource not found in testing"));
			
			Capture<Integer> captureCode = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(captureCode));
			
			EasyMock.replay(response, resourceMapMock, request);
			
			processor.process(request, response, resource);
			EasyMock.verify(response, resourceMapMock, request);
			
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
