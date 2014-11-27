package com.webpagebytes.cms.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;

import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import org.easymock.Capture;
import org.easymock.EasyMock;

import com.webpagebytes.cms.cache.WPBArticlesCache;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBFilesCache;
import com.webpagebytes.cms.cache.WPBMessagesCache;
import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cache.WPBProjectCache;
import com.webpagebytes.cms.cache.WPBUrisCache;
import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cache.WPBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.template.WPBFreeMarkerTemplateEngine;
import com.webpagebytes.cms.template.FreeMarkerTemplateLoader;
import com.webpagebytes.cms.template.FreeMarkerTemplateObject;
import com.webpagebytes.cms.template.WPBTemplateEngine;
import com.webpagebytes.cms.template.FreeMarkerTemplateObject.TemplateType;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerTemplateLoader {

private WPBUrisCache wbUriCacheMock;
private WPBWebPagesCache wbWebPageCacheMock;
private WPBWebPageModulesCache wbWebPageModuleCacheMock;
private WPBParametersCache wbParameterCacheMock;
private WPBFilesCache wbImageCacheMock;
private WPBArticlesCache wbArticleCacheMock;
private WPBMessagesCache wbMessageCacheMock;
private WPBProjectCache wbProjectCacheMock;

WPBCacheInstances cacheInstances;

@Before
public void setUp()
{
	wbUriCacheMock = PowerMock.createMock(WPBUrisCache.class);
	wbWebPageCacheMock = PowerMock.createMock(WPBWebPagesCache.class);
	wbWebPageModuleCacheMock = PowerMock.createMock(WPBWebPageModulesCache.class);
	wbParameterCacheMock = PowerMock.createMock(WPBParametersCache.class);
	wbImageCacheMock = PowerMock.createMock(WPBFilesCache.class);
	wbArticleCacheMock = PowerMock.createMock(WPBArticlesCache.class);
	wbMessageCacheMock = PowerMock.createMock(WPBMessagesCache.class);
	wbProjectCacheMock = PowerMock.createMock(WPBProjectCache.class);
	cacheInstances = new WPBCacheInstances(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
	
}

@Test
public void test_findTemplateSource_page_ok()
{
	try
	{

		String templatePage = WPBTemplateEngine.WEBPAGES_PATH_PREFIX + "test";
	
		WBWebPage webPageMock = PowerMock.createMock(WBWebPage.class);
		Date date = new Date();
		EasyMock.expect(webPageMock.getLastModified()).andReturn(date);
		EasyMock.expect(wbWebPageCacheMock.get("test")).andReturn(webPageMock);
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, webPageMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);

		PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, webPageMock);
		
		assertTrue(object.getName().equals("test"));
		assertTrue(object.getType() == TemplateType.TEMPLATE_PAGE);
		assertTrue(object.getLastModified() == date.getTime());
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_findTemplateSource_page_notfound()
{
	try
	{

		String templatePage = WPBTemplateEngine.WEBPAGES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageCacheMock.get("test")).andReturn(null);	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
		PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);		
		assertTrue(object == null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_findTemplateSource_page_exceptionGetFromCache()
{
	try
	{

		String templatePage = WPBTemplateEngine.WEBPAGES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageCacheMock.get("test")).andThrow(new WPBIOException(""));
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
		PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);

		assertTrue(object == null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_findTemplateSource_module_ok()
{
	try
	{
		String templatePage = WPBTemplateEngine.WEBMODULES_PATH_PREFIX + "test";
	
		WBWebPageModule webPageModuleMock = PowerMock.createMock(WBWebPageModule.class);
		Date date = new Date();
		EasyMock.expect(webPageModuleMock.getLastModified()).andReturn(date);
		EasyMock.expect(wbWebPageModuleCacheMock.get("test")).andReturn(webPageModuleMock);
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, webPageModuleMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);

		PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, webPageModuleMock);
		assertTrue(object.getName().equals("test"));
		assertTrue(object.getType() == TemplateType.TEMPLATE_MODULE);
		assertTrue(object.getLastModified() == date.getTime());
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_findTemplateSource_module_notfound()
{
	try
	{

		String templatePage = WPBTemplateEngine.WEBMODULES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageModuleCacheMock.get("test")).andReturn(null);
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		assertTrue(object == null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_findTemplateSource_module_exceptionGetFromCache()
{
	try
	{

		String templatePage = WPBTemplateEngine.WEBMODULES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageModuleCacheMock.get("test")).andThrow(new WPBIOException(""));
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
		PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);

		assertTrue(object == null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_findTemplateSource_wrongNameFormat()
{
	try
	{
		String templatePage =  "random test";
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
		FreeMarkerTemplateObject object = (FreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
		PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);

		assertTrue(object == null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_module()
{
	try
	{
	String templateName = "testX";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPageModule pageModuleMock = PowerMock.createMock(WBWebPageModule.class);
	EasyMock.expect(pageModuleMock.getLastModified()).andReturn(date);
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(pageModuleMock);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, pageModuleMock, templateObjectMock);

	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	long lastmodified = templateLoader.getLastModified(templateObjectMock);
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	
	assertTrue (lastmodified == date.getTime());
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_module_exception()
{
	try
	{
	String templateName = "testX";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andThrow(new WPBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);

	long lastmodified = templateLoader.getLastModified(templateObjectMock);
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	
	assertTrue (lastmodified == 0L);
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_module_notfound()
{
	try
	{
	String templateName = "testX";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);

	long lastmodified = templateLoader.getLastModified(templateObjectMock);
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	
	assertTrue (lastmodified == 0L);
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_page()
{
	try
	{
	String templateName = "testX";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPage pageMock = PowerMock.createMock(WBWebPage.class);
	EasyMock.expect(pageMock.getLastModified()).andReturn(date);
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(pageMock);

	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, pageMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	long lastmodified = templateLoader.getLastModified(templateObjectMock);
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, pageMock, templateObjectMock);	
	assertTrue (lastmodified == date.getTime());
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_page_exception()
{
	try
	{
	String templateName = "testX";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andThrow(new WPBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);

	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	long lastmodified = templateLoader.getLastModified(templateObjectMock);
	
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	assertTrue (lastmodified == 0L);
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_page_notfound()
{
	try
	{
	String templateName = "testX";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	long lastmodified = templateLoader.getLastModified(templateObjectMock);
	
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);

	assertTrue (lastmodified == 0L);
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getLastModified_null()
{
	try
	{
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	long lastmodified = templateLoader.getLastModified(null);
	
	assertTrue (lastmodified == 0L);
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_page()
{
	try
	{
	String templateName = "testX";
	String htmlSource = "<b>abc</b>";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPage pageMock = PowerMock.createMock(WBWebPage.class);
	EasyMock.expect(pageMock.getHtmlSource()).andReturn(htmlSource);
	
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(pageMock);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock, pageMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	Reader reader = templateLoader.getReader(templateObjectMock, "");
	BufferedReader strReader = new BufferedReader(reader);
	assertTrue(strReader.readLine().equals(htmlSource));
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock, pageMock);
	
	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_module()
{
	try
	{
	String templateName = "testX";
	String htmlSource = "<b>abc</b>";
	Date date = new Date();
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPageModule pageModuleMock = PowerMock.createMock(WBWebPageModule.class);
	EasyMock.expect(pageModuleMock.getHtmlSource()).andReturn(htmlSource);
	
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(pageModuleMock);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock, pageModuleMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	Reader reader = templateLoader.getReader(templateObjectMock, "");
	BufferedReader strReader = new BufferedReader(reader);
	assertTrue(strReader.readLine().equals(htmlSource));
	PowerMock.verify(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock, pageModuleMock);

	}catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_module_exception()
{
	try
	{
	String templateName = "testX";
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andThrow(new WPBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	templateLoader.getReader(templateObjectMock, "");
	assertTrue(false);
	
	
	}
	catch (IOException e)
	{
		
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_page_exception()
{
	try
	{
	String templateName = "testX";
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andThrow(new WPBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	templateLoader.getReader(templateObjectMock, "");
	assertTrue(false);
	
	}
	catch (IOException e)
	{
		
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_page_notfound()
{
	try
	{
	String templateName = "testX";
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	templateLoader.getReader(templateObjectMock, "");
	assertTrue(false);
	
	}
	catch (IOException e)
	{
		
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_module_notfound()
{
	try
	{
	String templateName = "testX";
	
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	templateLoader.getReader(templateObjectMock, "");
	assertTrue(false);
	
	}
	catch (IOException e)
	{
		
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getReader_null()
{
	try
	{
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
		templateLoader.getReader(null, "");
		assertTrue(false);
	
	}
	catch (IOException e)
	{
		
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getPrefix()
{
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	
	assertTrue (templateLoader.getWebModulesPathPrefix().equals(WPBFreeMarkerTemplateEngine.WEBMODULES_PATH_PREFIX));
	assertTrue (templateLoader.getWebPagesPathPrefix().equals(WPBFreeMarkerTemplateEngine.WEBPAGES_PATH_PREFIX));
		
}

@Test
public void test_closeTemplateSource()
{
	FreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(FreeMarkerTemplateObject.class);	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader(cacheInstances);
	try
	{
		templateLoader.closeTemplateSource(templateObjectMock);
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_defaultConstructor()
{
	FreeMarkerTemplateLoader templateLoader = new FreeMarkerTemplateLoader();
	assertTrue(templateLoader != null);
}

}
