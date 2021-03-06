package com.webpagebytes.cms.engine;

import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.Before;
import org.easymock.EasyMock;
import org.easymock.Capture;

import static org.junit.Assert.*;

import com.webpagebytes.cms.WPBAdminServlet;
import com.webpagebytes.cms.WPBCmsContextListener;
import com.webpagebytes.cms.engine.AdminRequestProcessorFactory;
import com.webpagebytes.cms.engine.AjaxRequestProcessor;
import com.webpagebytes.cms.engine.ResourceRequestProcessor;
import com.webpagebytes.cms.engine.WPBServletUtility;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

public class TestAdminServlet {

	private HttpServletRequest request;
	private HttpServletResponse response;	
	private ResourceRequestProcessor resourceProcessor;
	private AjaxRequestProcessor ajaxProcessor;
	private WPBServletUtility servletUtility;
	private WPBAdminServlet adminServlet;
	
	@Before
	public void setUp()
	{
		request = EasyMock.createMock(HttpServletRequest.class);
		response = EasyMock.createMock(HttpServletResponse.class);		
		resourceProcessor = EasyMock.createMock(ResourceRequestProcessor.class);
		ajaxProcessor = EasyMock.createMock(AjaxRequestProcessor.class);
		servletUtility = EasyMock.createMock(WPBServletUtility.class);
		adminServlet = new WPBAdminServlet();		
		CmsConfigurationFactory.setConfigPath("wbConfiguration.xml");
	}
	
	@Test
	public void testGetAdminRelativeUri()
	{
		try
		{
			WPBAdminServlet adminServlet = new WPBAdminServlet();
			adminServlet.setAdminURIPart("/admin");
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/js/base.js");
			EasyMock.replay(request);
			
			String strResponse = adminServlet.getAdminRelativeUri(request);
			assertTrue(strResponse.compareTo("/js/base.js") == 0);
			
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testGetAdminRelativeUriWrongUri()
	{
		try
		{
			WPBAdminServlet adminServlet = new WPBAdminServlet();
			adminServlet.setAdminURIPart("/admin");
			EasyMock.expect(request.getRequestURI()).andReturn("/test/script.js");
			EasyMock.replay(request);
			
			String strResponse = adminServlet.getAdminRelativeUri(request);
			
			// we should not get here
			assertTrue(false);
		} catch (Exception e)
		{
		}
	}


	@Test
	public void testInit_ok_adminPath_null()
	{
		adminServlet.setAdminURIPart(null);
		testInit_ok();
	}

	@Test
	public void testInit_ok_adminPath_notNull()
	{
		adminServlet.setAdminURIPart("/admin");
		try
		{
			
			AdminRequestProcessorFactory testProcessor = EasyMock.createMock(AdminRequestProcessorFactory.class);
			
			Capture<String> adminFolderR = new Capture<String>();
			Capture<String> adminFolderA = new Capture<String>();
			Capture<String> adminConfigR = new Capture<String>();
			Capture<String> adminConfigA = new Capture<String>();
			Capture<String> adminUriPart = new Capture<String>();
			
			resourceProcessor.initialize(EasyMock.capture(adminFolderR), EasyMock.capture(adminConfigR));
			ajaxProcessor.initialize(EasyMock.capture(adminFolderA), EasyMock.capture(adminConfigA));
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn("/admin");
			
			adminServlet.setServletUtility(servletUtility);
			
			EasyMock.expect(testProcessor.createAjaxRequestProcessor()).andReturn(ajaxProcessor);
			EasyMock.expect(testProcessor.createResourceRequestProcessor()).andReturn(resourceProcessor);
			ajaxProcessor.setAdminUriPart(EasyMock.capture(adminUriPart));
			adminServlet.setProcessorFactory(testProcessor);
			EasyMock.replay(testProcessor, servletUtility, resourceProcessor, ajaxProcessor);
			
			adminServlet.init();
			EasyMock.verify(testProcessor, resourceProcessor, ajaxProcessor);
			assertTrue(adminUriPart.getValue().compareTo("/admin") == 0);
			assertTrue(adminFolderR.getValue().compareTo(WPBAdminServlet.ADMIN_RESOURCE_FOLDER) == 0);
			assertTrue(adminFolderA.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_FOLDER) == 0);
			assertTrue(adminConfigA.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_AJAX) == 0);
			assertTrue(adminConfigR.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_RESOURCES) == 0);
			
		}
		catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testInit_ok_adminPath_endswithshash()
	{
		try
		{
			
			AdminRequestProcessorFactory testProcessor = EasyMock.createMock(AdminRequestProcessorFactory.class);
			
			Capture<String> adminFolderR = new Capture<String>();
			Capture<String> adminFolderA = new Capture<String>();
			Capture<String> adminConfigR = new Capture<String>();
			Capture<String> adminConfigA = new Capture<String>();
			Capture<String> adminUriPart = new Capture<String>();
			
			resourceProcessor.initialize(EasyMock.capture(adminFolderR), EasyMock.capture(adminConfigR));
			ajaxProcessor.initialize(EasyMock.capture(adminFolderA), EasyMock.capture(adminConfigA));
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn("/admin/");
			adminServlet.setServletUtility(servletUtility);
			EasyMock.expect(testProcessor.createAjaxRequestProcessor()).andReturn(ajaxProcessor);
			EasyMock.expect(testProcessor.createResourceRequestProcessor()).andReturn(resourceProcessor);
			ajaxProcessor.setAdminUriPart(EasyMock.capture(adminUriPart));
			adminServlet.setProcessorFactory(testProcessor);
			EasyMock.replay(testProcessor, resourceProcessor, ajaxProcessor, servletUtility);
			
			adminServlet.init();
			EasyMock.verify(testProcessor, resourceProcessor, ajaxProcessor, servletUtility);
			assertTrue(adminServlet.getAdminURIPart().compareTo("/admin") == 0);
			assertTrue(adminFolderR.getValue().compareTo(WPBAdminServlet.ADMIN_RESOURCE_FOLDER) == 0);
			assertTrue(adminFolderA.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_FOLDER) == 0);
			assertTrue(adminConfigA.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_AJAX) == 0);
			assertTrue(adminConfigR.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_RESOURCES) == 0);
			
		}
		catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testInit_ok()
	{
		try
		{
			
			AdminRequestProcessorFactory testProcessor = EasyMock.createMock(AdminRequestProcessorFactory.class);
			
			Capture<String> adminFolderR = new Capture<String>();
			Capture<String> adminFolderA = new Capture<String>();
			Capture<String> adminConfigR = new Capture<String>();
			Capture<String> adminConfigA = new Capture<String>();
			Capture<String> adminUriPart = new Capture<String>();
			
			resourceProcessor.initialize(EasyMock.capture(adminFolderR), EasyMock.capture(adminConfigR));
			ajaxProcessor.initialize(EasyMock.capture(adminFolderA), EasyMock.capture(adminConfigA));
			ajaxProcessor.setAdminUriPart(EasyMock.capture(adminUriPart));
			EasyMock.expect(testProcessor.createAjaxRequestProcessor()).andReturn(ajaxProcessor);
			EasyMock.expect(testProcessor.createResourceRequestProcessor()).andReturn(resourceProcessor);
			
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn("/admin");
			
			adminServlet.setServletUtility(servletUtility);
			adminServlet.setProcessorFactory(testProcessor);
			EasyMock.replay(testProcessor, resourceProcessor, ajaxProcessor, servletUtility);
			
			adminServlet.init();
			EasyMock.verify(testProcessor, resourceProcessor, ajaxProcessor, servletUtility);
			assertTrue(adminUriPart.getValue().compareTo("/admin") == 0);
			assertTrue(adminFolderR.getValue().compareTo(WPBAdminServlet.ADMIN_RESOURCE_FOLDER) == 0);
			assertTrue(adminFolderA.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_FOLDER) == 0);
			assertTrue(adminConfigA.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_AJAX) == 0);
			assertTrue(adminConfigR.getValue().compareTo(WPBAdminServlet.ADMIN_CONFIG_RESOURCES) == 0);
			
			assertTrue(adminServlet.getProcessorFactory() != null);
			assertTrue(adminServlet.getResourceRequestProcessor() != null);
			assertTrue(adminServlet.getAjaxRequestProcssor() != null);
			assertTrue(adminServlet.getServletUtility() != null);
			assertTrue(adminServlet.getAdminURIPart() != null);
		}
		catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testInit_exception()
	{
		try
		{
			
			AdminRequestProcessorFactory testProcessor = EasyMock.createMock(AdminRequestProcessorFactory.class);
			
			Capture<String> adminFolderR = new Capture<String>();
			Capture<String> adminFolderA = new Capture<String>();
			Capture<String> adminConfigR = new Capture<String>();
			Capture<String> adminConfigA = new Capture<String>();
			
			resourceProcessor.initialize(EasyMock.capture(adminFolderR), EasyMock.capture(adminConfigR));
			EasyMock.expectLastCall().andThrow(new WPBException(""));
			
			EasyMock.expect(testProcessor.createResourceRequestProcessor()).andReturn(resourceProcessor);
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn("/admin");
			
			adminServlet.setServletUtility(servletUtility);
			adminServlet.setProcessorFactory(testProcessor);
			EasyMock.replay(testProcessor, resourceProcessor, servletUtility);
			
			adminServlet.init();
			assertTrue(false);
		}
		catch (Exception e)
		{
			if (!(e instanceof ServletException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testInit_adminUri_null()
	{
		try
		{
			adminServlet.setAdminURIPart("");
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn(null);
			
			adminServlet.setServletUtility(servletUtility);
			EasyMock.replay( servletUtility);
			
			adminServlet.init();
			assertTrue(false);			
		}
		catch (Exception e)
		{
			if (!(e instanceof ServletException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testInit_adminUri_empty()
	{
		try
		{
			adminServlet.setAdminURIPart("");
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn("");
			adminServlet.setServletUtility(servletUtility);
			EasyMock.replay( servletUtility);
			
			adminServlet.init();
			assertTrue(false);			
		}
		catch (Exception e)
		{
			if (!(e instanceof ServletException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testInit_null_getInitParameter()
	{
		try
		{
			WPBAdminServlet adminServlet = new WPBAdminServlet();
			adminServlet.setAdminURIPart(null);
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn(null);
			adminServlet.setServletUtility(servletUtility);
			EasyMock.replay(servletUtility);
			
			adminServlet.init();
			assertTrue(false);
		}
		catch (Exception e)
		{
			if (!(e instanceof ServletException))
			{
				assertTrue(false);
			}			
		}
	}

	
	@Test
	public void testInit_emptyAdminUri()
	{
		try
		{
			WPBAdminServlet adminServlet = new WPBAdminServlet();
			adminServlet.setAdminURIPart("");
			EasyMock.expect(servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, adminServlet)).andReturn("/path");
			EasyMock.expect(servletUtility.getInitParameter(WPBAdminServlet.ADMIN_URI_PREFIX, adminServlet)).andReturn(null);
			
			adminServlet.setServletUtility(servletUtility);
			EasyMock.replay( servletUtility);
			adminServlet.init();
			assertTrue(false);
		}
		catch (Exception e)
		{
			if (!(e instanceof ServletException))
			{
				assertTrue(false);
			}			
		}
	}

	
	@Test
	public void testDecorateAdminRelativeUri()
	{
		try
		{
			WPBAdminServlet adminServlet = new WPBAdminServlet();
			adminServlet.setAdminURIPart("/admin");
			
			String uri = adminServlet.decorateAdminRelativeUri("");
			assertTrue(uri.compareTo("/index.html") == 0);
			
			String uri2 = adminServlet.decorateAdminRelativeUri("/");
			assertTrue(uri2.compareTo("/index.html") == 0);
			
			String uri3 = adminServlet.decorateAdminRelativeUri("/js/base.js");
			assertTrue(uri3.compareTo("/js/base.js") == 0);
			
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}
	
	@Test
	public void testDoGet_resource_ok()
	{
		try
		{
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/js/base.js");
			adminServlet.setAdminURIPart("/admin");		
			EasyMock.expect(ajaxProcessor.isAjaxRequest(request, "/js/base.js")).andReturn(false);
			EasyMock.expect(resourceProcessor.isResourceRequest("/js/base.js")).andReturn(true);
			
			Capture<HttpServletRequest> captureReq = new Capture<HttpServletRequest>();
			Capture<HttpServletResponse> captureResp = new Capture<HttpServletResponse>();
			Capture<String> captureUri = new Capture<String>();
			resourceProcessor.process(EasyMock.capture(captureReq), EasyMock.capture(captureResp), EasyMock.capture(captureUri));		
			adminServlet.setResourceRequestProcessor(resourceProcessor);
			adminServlet.setAjaxRequestProcssor(ajaxProcessor);
			EasyMock.replay(ajaxProcessor, resourceProcessor, request);
			
			adminServlet.doGet(request, response);						
			assertTrue (captureReq.getValue() == request);
			assertTrue (captureResp.getValue() == response);
			assertTrue (captureUri.getValue().compareTo("/js/base.js") == 0);
			EasyMock.verify(resourceProcessor, request);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testDoGet_ajax_ok()
	{
		try
		{
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/pages");
			adminServlet.setAdminURIPart("/admin");		
			EasyMock.expect(ajaxProcessor.isAjaxRequest(request, "/pages")).andReturn(true);
			
			Capture<HttpServletRequest> captureReq = new Capture<HttpServletRequest>();
			Capture<HttpServletResponse> captureResp = new Capture<HttpServletResponse>();
			Capture<String> captureUri = new Capture<String>();
			ajaxProcessor.process(EasyMock.capture(captureReq), EasyMock.capture(captureResp), EasyMock.capture(captureUri));		
			adminServlet.setResourceRequestProcessor(resourceProcessor);
			adminServlet.setAjaxRequestProcssor(ajaxProcessor);
			EasyMock.replay(resourceProcessor, ajaxProcessor, request);
			
			adminServlet.doGet(request, response);						
			assertTrue (captureReq.getValue() == request);
			assertTrue (captureResp.getValue() == response);
			assertTrue (captureUri.getValue().compareTo("/pages") == 0);
			EasyMock.verify(resourceProcessor, ajaxProcessor, request);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testDoGet_not_found()
	{
		try
		{
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/pages");
			adminServlet.setAdminURIPart("/admin");		
			EasyMock.expect(resourceProcessor.isResourceRequest("/pages")).andReturn(false);
			EasyMock.expect(ajaxProcessor.isAjaxRequest(request, "/pages")).andReturn(false);
			
			Capture<Integer> captureCode = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(captureCode));
			adminServlet.setResourceRequestProcessor(resourceProcessor);
			adminServlet.setAjaxRequestProcssor(ajaxProcessor);
			EasyMock.replay(resourceProcessor, ajaxProcessor, request, response);
			
			adminServlet.doGet(request, response);						
			assertTrue (captureCode.getValue() == HttpServletResponse.SC_NOT_FOUND);
			EasyMock.verify(resourceProcessor, ajaxProcessor, request, response);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	
	private void testAjaxCalls(String methodName)
	{
		try
		{
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/pages");
			adminServlet.setAdminURIPart("/admin");		
			EasyMock.expect(ajaxProcessor.isAjaxRequest(request, "/pages")).andReturn(true);
			
			Capture<HttpServletRequest> captureReq = new Capture<HttpServletRequest>();
			Capture<HttpServletResponse> captureResp = new Capture<HttpServletResponse>();
			Capture<String> captureUri = new Capture<String>();
			ajaxProcessor.process(EasyMock.capture(captureReq), EasyMock.capture(captureResp), EasyMock.capture(captureUri));		
			adminServlet.setAjaxRequestProcssor(ajaxProcessor);
			EasyMock.replay(ajaxProcessor, request);
		
		    Method m = adminServlet.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
	        m.invoke(adminServlet, request, response);

			assertTrue (captureReq.getValue() == request);
			assertTrue (captureResp.getValue() == response);
			assertTrue (captureUri.getValue().compareTo("/pages") == 0);
			EasyMock.verify(ajaxProcessor, request);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testDoAjax_ok()
	{
		testAjaxCalls("doAjax");
	}

	@Test
	public void testDoPost_ok()
	{
		testAjaxCalls("doPost");
	}

	@Test
	public void testDoPut_ok()
	{
		testAjaxCalls("doPut");
	}
	
	@Test
	public void testDoDelete_ok()
	{
		testAjaxCalls("doDelete");
	}

	@Test
	public void testDoAjax_not_found()
	{
		try
		{
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/pages");
			adminServlet.setAdminURIPart("/admin");		
			EasyMock.expect(ajaxProcessor.isAjaxRequest(request, "/pages")).andReturn(false);
			
			Capture<Integer> captureCode = new Capture<Integer>();
			response.setStatus(EasyMock.captureInt(captureCode));
			adminServlet.setAjaxRequestProcssor(ajaxProcessor);
			EasyMock.replay(ajaxProcessor, request, response);
			
			adminServlet.doAjax(request, response);						
			assertTrue (captureCode.getValue() == HttpServletResponse.SC_NOT_FOUND);
			EasyMock.verify(ajaxProcessor, request, response);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testDoAjax_exception()
	{
		try
		{
			EasyMock.expect(request.getRequestURI()).andReturn("/admin/pages");
			adminServlet.setAdminURIPart("/admin");		
			EasyMock.expect(ajaxProcessor.isAjaxRequest(request, "/pages")).andReturn(true);
			ajaxProcessor.process(request, response, "/pages");
			EasyMock.expectLastCall().andThrow(new WPBException(""));
			
			adminServlet.setAjaxRequestProcssor(ajaxProcessor);
			EasyMock.replay(ajaxProcessor, request, response);
			
			adminServlet.doAjax(request, response);						
			assertFalse(true);
		} catch (Exception e)
		{
			if (!(e instanceof ServletException))
			{
				assertTrue(false);
			}
		}
	}

}
