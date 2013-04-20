package com.webbricks.template;

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

import com.webbricks.cache.WBArticleCache;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBImageCache;
import com.webbricks.cache.WBMessageCache;
import com.webbricks.cache.WBParameterCache;
import com.webbricks.cache.WBProjectCache;
import com.webbricks.cache.WBUriCache;
import com.webbricks.cache.WBWebPageCache;
import com.webbricks.cache.WBWebPageModuleCache;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;
import com.webbricks.template.WBFreeMarkerTemplateObject.TemplateType;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerTemplateLoader {

private WBUriCache wbUriCacheMock;
private WBWebPageCache wbWebPageCacheMock;
private WBWebPageModuleCache wbWebPageModuleCacheMock;
private WBParameterCache wbParameterCacheMock;
private WBImageCache wbImageCacheMock;
private WBArticleCache wbArticleCacheMock;
private WBMessageCache wbMessageCacheMock;
private WBProjectCache wbProjectCacheMock;

WBCacheInstances cacheInstances;

@Before
public void setUp()
{
	wbUriCacheMock = PowerMock.createMock(WBUriCache.class);
	wbWebPageCacheMock = PowerMock.createMock(WBWebPageCache.class);
	wbWebPageModuleCacheMock = PowerMock.createMock(WBWebPageModuleCache.class);
	wbParameterCacheMock = PowerMock.createMock(WBParameterCache.class);
	wbImageCacheMock = PowerMock.createMock(WBImageCache.class);
	wbArticleCacheMock = PowerMock.createMock(WBArticleCache.class);
	wbMessageCacheMock = PowerMock.createMock(WBMessageCache.class);
	wbProjectCacheMock = PowerMock.createMock(WBProjectCache.class);
	cacheInstances = new WBCacheInstances(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
	
}

@Test
public void test_findTemplateSource_page_ok()
{
	try
	{

		String templatePage = WBTemplateEngine.WEBPAGES_PATH_PREFIX + "test";
	
		WBWebPage webPageMock = PowerMock.createMock(WBWebPage.class);
		Date date = new Date();
		EasyMock.expect(webPageMock.getLastModified()).andReturn(date);
		EasyMock.expect(wbWebPageCacheMock.get("test")).andReturn(webPageMock);
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, webPageMock);
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);

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

		String templatePage = WBTemplateEngine.WEBPAGES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageCacheMock.get("test")).andReturn(null);	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
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

		String templatePage = WBTemplateEngine.WEBPAGES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageCacheMock.get("test")).andThrow(new WBIOException(""));
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
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
		String templatePage = WBTemplateEngine.WEBMODULES_PATH_PREFIX + "test";
	
		WBWebPageModule webPageModuleMock = PowerMock.createMock(WBWebPageModule.class);
		Date date = new Date();
		EasyMock.expect(webPageModuleMock.getLastModified()).andReturn(date);
		EasyMock.expect(wbWebPageModuleCacheMock.get("test")).andReturn(webPageModuleMock);
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, webPageModuleMock);
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);

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

		String templatePage = WBTemplateEngine.WEBMODULES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageModuleCacheMock.get("test")).andReturn(null);
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
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

		String templatePage = WBTemplateEngine.WEBMODULES_PATH_PREFIX + "test";
	
		EasyMock.expect(wbWebPageModuleCacheMock.get("test")).andThrow(new WBIOException(""));
	
		PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock);
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
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
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
		WBFreeMarkerTemplateObject object = (WBFreeMarkerTemplateObject) templateLoader.findTemplateSource(templatePage);
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPageModule pageModuleMock = PowerMock.createMock(WBWebPageModule.class);
	EasyMock.expect(pageModuleMock.getLastModified()).andReturn(date);
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(pageModuleMock);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, pageModuleMock, templateObjectMock);

	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andThrow(new WBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);

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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);

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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPage pageMock = PowerMock.createMock(WBWebPage.class);
	EasyMock.expect(pageMock.getLastModified()).andReturn(date);
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(pageMock);

	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, pageMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andThrow(new WBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);

	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	//EasyMock.expect(templateObject.getLastModified()).andReturn(date.getTime());
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPage pageMock = PowerMock.createMock(WBWebPage.class);
	EasyMock.expect(pageMock.getHtmlSource()).andReturn(htmlSource);
	
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(pageMock);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock, pageMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
	
	WBWebPageModule pageModuleMock = PowerMock.createMock(WBWebPageModule.class);
	EasyMock.expect(pageModuleMock.getHtmlSource()).andReturn(htmlSource);
	
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(pageModuleMock);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock, pageModuleMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andThrow(new WBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andThrow(new WBIOException(""));
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_PAGE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);
	EasyMock.expect(templateObjectMock.getType()).andReturn(TemplateType.TEMPLATE_MODULE);
	
	EasyMock.expect(templateObjectMock.getName()).andReturn(templateName);
		
	EasyMock.expect(wbWebPageModuleCacheMock.get(templateName)).andReturn(null);
	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
		WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
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
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
	
	assertTrue (templateLoader.getWebModulesPathPrefix().equals(WBFreeMarkerTemplateEngine.WEBMODULES_PATH_PREFIX));
	assertTrue (templateLoader.getWebPagesPathPrefix().equals(WBFreeMarkerTemplateEngine.WEBPAGES_PATH_PREFIX));
		
}

@Test
public void test_closeTemplateSource()
{
	WBFreeMarkerTemplateObject templateObjectMock = PowerMock.createMock(WBFreeMarkerTemplateObject.class);	
	PowerMock.replay(wbUriCacheMock, wbWebPageCacheMock, wbWebPageModuleCacheMock, wbParameterCacheMock, wbImageCacheMock, wbArticleCacheMock, wbMessageCacheMock, wbProjectCacheMock, templateObjectMock);
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader(cacheInstances);
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
	WBFreeMarkerTemplateLoader templateLoader = new WBFreeMarkerTemplateLoader();
	assertTrue(templateLoader != null);
}

}
