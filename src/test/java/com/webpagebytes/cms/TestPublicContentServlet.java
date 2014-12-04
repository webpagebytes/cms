package com.webpagebytes.cms;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.WPBPublicContentServlet;
import com.webpagebytes.cms.WPBServletUtility;
import com.webpagebytes.cms.appinterfaces.WPBArticlesCache;
import com.webpagebytes.cms.appinterfaces.WPBFilesCache;
import com.webpagebytes.cms.appinterfaces.WPBMessagesCache;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.appinterfaces.WPBParametersCache;
import com.webpagebytes.cms.appinterfaces.WPBProjectCache;
import com.webpagebytes.cms.appinterfaces.WPBUrisCache;
import com.webpagebytes.cms.appinterfaces.WPBPageModulesCache;
import com.webpagebytes.cms.appinterfaces.WPBWebPagesCache;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WPBPublicContentServlet.class})
public class TestPublicContentServlet {

WPBPublicContentServlet publicServlet;
HttpServletRequest requestMock;
HttpServletResponse responseMock;
WPBCacheInstances cacheInstances;
WPBUrisCache urisCacheMock;
WPBParametersCache parametersCacheMock;
WPBFilesCache filesCacheMock;
WPBMessagesCache messagesCacheMock;
WPBArticlesCache articlesCacheMock;
WPBWebPagesCache pagesCacheMock;
WPBPageModulesCache modulesCacheMock;
WPBProjectCache projectCacheMock;
WPBCacheFactory cacheFactoryMock;
Logger loggerMock;

@Before
public void setUp()
{
publicServlet = new WPBPublicContentServlet();

loggerMock = EasyMock.createMock(Logger.class);
Whitebox.setInternalState(WPBPublicContentServlet.class, "log", loggerMock);

requestMock = EasyMock.createMock(HttpServletRequest.class);
responseMock = EasyMock.createMock(HttpServletResponse.class);

urisCacheMock = EasyMock.createMock(WPBUrisCache.class);
parametersCacheMock = EasyMock.createMock(WPBParametersCache.class);
filesCacheMock = EasyMock.createMock(WPBFilesCache.class);
messagesCacheMock = EasyMock.createMock(WPBMessagesCache.class);
articlesCacheMock = EasyMock.createMock(WPBArticlesCache.class);
pagesCacheMock = EasyMock.createMock(WPBWebPagesCache.class);
modulesCacheMock = EasyMock.createMock(WPBPageModulesCache.class);
projectCacheMock = EasyMock.createMock(WPBProjectCache.class);

cacheInstances = new WPBCacheInstances(urisCacheMock, pagesCacheMock, modulesCacheMock, parametersCacheMock, filesCacheMock, articlesCacheMock, messagesCacheMock, projectCacheMock); 

Whitebox.setInternalState(CmsConfigurationFactory.class, "configuration", (Object) null);
Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", (Object) null);	

}

@After
public void tearDown()
{
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configuration", (Object) null);
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", (Object) null);	
}

@Test
public void test_doGet()
{
	suppress(method(WPBPublicContentServlet.class, "handleRequest"));
	try
	{
		publicServlet.doGet(requestMock, responseMock);
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_doPut()
{
	suppress(method(WPBPublicContentServlet.class, "handleRequest"));
	try
	{
		publicServlet.doPut(requestMock, responseMock);
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_doPost()
{
	suppress(method(WPBPublicContentServlet.class, "handleRequest"));
	try
	{
		publicServlet.doPost(requestMock, responseMock);
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_doDelete()
{
	suppress(method(WPBPublicContentServlet.class, "handleRequest"));
	try
	{
		publicServlet.doDelete(requestMock, responseMock);
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_init_exception()
{
	suppress(method(WPBPublicContentServlet.class, "initBuilders"));
	suppress(method(WPBPublicContentServlet.class, "initLocalFileContentBuilder"));

	cacheFactoryMock = EasyMock.createMock(WPBCacheFactory.class);
	EasyMock.expect(cacheFactoryMock.getUrisCacheInstance()).andReturn(urisCacheMock);
	EasyMock.expect(cacheFactoryMock.getWebPagesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getPageModulesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getArticlesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getMessagesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getFilesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getParametersCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getProjectCacheInstance()).andReturn(null);
	
	WPBServletUtility servletUtilityMock = EasyMock.createMock(WPBServletUtility.class);	
	EasyMock.expect(servletUtilityMock.getContextPath(publicServlet)).andReturn("/");		
	EasyMock.expect(servletUtilityMock.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, publicServlet)).andReturn("META-INF/wbconfiguration.xml");
	Whitebox.setInternalState(publicServlet, "servletUtility", servletUtilityMock);
	Whitebox.setInternalState(publicServlet, "cacheInstances", cacheInstances);
	Whitebox.setInternalState(publicServlet, "cacheFactory", cacheFactoryMock);
	
	try
	{
		EasyMock.expect(urisCacheMock.getAllUris(0)).andThrow(new WPBIOException(""));
		EasyMock.replay(requestMock, responseMock, servletUtilityMock, urisCacheMock, cacheFactoryMock);
		publicServlet.init();
		
	} catch (ServletException e)
	{
		// OK
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	EasyMock.verify(requestMock, responseMock, servletUtilityMock);
		
}

@Test
public void test_init()
{
	suppress(method(WPBPublicContentServlet.class, "initUrls"));
	suppress(method(WPBPublicContentServlet.class, "initBuilders"));
	suppress(method(WPBPublicContentServlet.class, "initLocalFileContentBuilder"));
	cacheFactoryMock = EasyMock.createMock(WPBCacheFactory.class);
	EasyMock.expect(cacheFactoryMock.getUrisCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getWebPagesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getPageModulesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getArticlesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getMessagesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getFilesCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getParametersCacheInstance()).andReturn(null);
	EasyMock.expect(cacheFactoryMock.getProjectCacheInstance()).andReturn(null);

	ServletConfig configMock = EasyMock.createMock(ServletConfig.class);
	WPBServletUtility servletUtilityMock = EasyMock.createMock(WPBServletUtility.class);	
	EasyMock.expect(servletUtilityMock.getContextPath(publicServlet)).andReturn("/test");		
	EasyMock.expect(servletUtilityMock.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, publicServlet)).andReturn("META-INF/wbconfiguration.xml");
	
	Whitebox.setInternalState(publicServlet, "servletUtility", servletUtilityMock);
	Whitebox.setInternalState(publicServlet, "cacheInstances", cacheInstances);
	Whitebox.setInternalState(publicServlet, "cacheFactory", cacheFactoryMock);
	
	try
	{		
		EasyMock.replay(requestMock, responseMock, servletUtilityMock, configMock, urisCacheMock, cacheFactoryMock);
		publicServlet.init(configMock);
		
	} 
	catch (Exception e)
	{
		assertTrue (false);
	}
	EasyMock.verify(requestMock, responseMock, servletUtilityMock, configMock, cacheFactoryMock);
	
	
}

@Test
public void test_handleRequestTypeText()
{
	try
	{
	WPBWebPage pageMock = EasyMock.createMock(WPBWebPage.class);
	InternalModel modelMock = EasyMock.createMock(InternalModel.class);
	PageContentBuilder pageBuilderMock = EasyMock.createMock(PageContentBuilder.class);
	Whitebox.setInternalState(publicServlet, "pageContentBuilder", pageBuilderMock);
	String content = "aContent";
	EasyMock.expect(pageBuilderMock.buildPageContent(requestMock, pageMock, modelMock)).andReturn(content);
	responseMock.setCharacterEncoding("UTF-8");
	EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);
	responseMock.addHeader("cache-control", "no-cache;no-store;");
	String contentType="plain/text";
	EasyMock.expect(pageMock.getContentType()).andReturn(contentType);	
	responseMock.setContentType(contentType);
	ServletOutputStream sos_ = EasyMock.createMock(ServletOutputStream.class);
	CacheServletOutputStream cacheOutputStream = new CacheServletOutputStream(sos_);
	EasyMock.expect(responseMock.getOutputStream()).andReturn(cacheOutputStream);
	Capture<byte[]> capture = new Capture<byte[]>();
	sos_.write(EasyMock.capture(capture));
	EasyMock.replay(requestMock, responseMock, pageMock, modelMock, sos_, pageBuilderMock);
	Whitebox.invokeMethod(publicServlet, "handleRequestTypeText", pageMock, requestMock, responseMock, modelMock);
	
	assertTrue((new String(capture.getValue())).equals(content));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_handleRequestTypeText_cache_0()
{
	handleRequestTypeText_cache(0);
}

@Test
public void test_handleRequestTypeText_cache_null()
{
	handleRequestTypeText_cache(null);
}

private void handleRequestTypeText_cache(Integer templateSource)
{
	try
	{
	WPBWebPage pageMock = EasyMock.createMock(WPBWebPage.class);
	InternalModel modelMock = EasyMock.createMock(InternalModel.class);
	PageContentBuilder pageBuilderMock = EasyMock.createMock(PageContentBuilder.class);
	Whitebox.setInternalState(publicServlet, "pageContentBuilder", pageBuilderMock);
	String content = "aContent";
	EasyMock.expect(pageBuilderMock.buildPageContent(requestMock, pageMock, modelMock)).andReturn(content);
	responseMock.setCharacterEncoding("UTF-8");
	EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(0);
	
	Long hash = 123L;
	EasyMock.expect(requestMock.getParameter(WPBPublicContentServlet.CACHE_QUERY_PARAM)).andReturn(hash.toString());
	EasyMock.expect(pageMock.getHash()).andReturn(hash);
	
	responseMock.addHeader("cache-control", "max-age=86400");
	String contentType="plain/text";
	EasyMock.expect(pageMock.getContentType()).andReturn(contentType);	
	responseMock.setContentType(contentType);
	ServletOutputStream sos_ = EasyMock.createMock(ServletOutputStream.class);
	CacheServletOutputStream cacheOutputStream = new CacheServletOutputStream(sos_);
	EasyMock.expect(responseMock.getOutputStream()).andReturn(cacheOutputStream);
	Capture<byte[]> capture = new Capture<byte[]>();
	sos_.write(EasyMock.capture(capture));
	EasyMock.replay(requestMock, responseMock, pageMock, modelMock, sos_, pageBuilderMock);
	Whitebox.invokeMethod(publicServlet, "handleRequestTypeText", pageMock, requestMock, responseMock, modelMock);
	
	assertTrue((new String(capture.getValue())).equals(content));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}


@Test
public void test_handleRequestTypeText_no_page()
{
	try
	{
	InternalModel modelMock = EasyMock.createMock(InternalModel.class);
	responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND);
	EasyMock.replay(requestMock, responseMock, modelMock);
	Whitebox.invokeMethod(publicServlet, "handleRequestTypeText", (WPBWebPage)null, requestMock, responseMock, modelMock);

	} catch (Exception e)
	{
		assertTrue(false);
	}
}

}
