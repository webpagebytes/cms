package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.WPBModel;
import com.webpagebytes.cms.WPBPageModelProvider;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.engine.InternalModel;
import com.webpagebytes.cms.engine.ModelBuilder;
import com.webpagebytes.cms.engine.PageContentBuilder;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBTemplateException;
import com.webpagebytes.cms.template.WPBTemplateEngine;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageContentBuilder.class})
public class TestPageContentBuilder {

WPBCacheInstances cacheInstancesMock;
WPBPagesCache pagesCacheMock;
PageContentBuilder pageContentBuilder;
ModelBuilder modelBuilderMock;
WPBTemplateEngine templateEngineMock;
HttpServletRequest requestMock;
WPBPage pageMock;
WPBProject projectMock;

@Before
public void setUp()
{
	cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
	pagesCacheMock = EasyMock.createMock(WPBPagesCache.class);
	modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
	pageContentBuilder = new PageContentBuilder(cacheInstancesMock, modelBuilderMock);
	templateEngineMock = EasyMock.createMock(WPBTemplateEngine.class);
	requestMock = EasyMock.createMock(HttpServletRequest.class);
	pageMock = EasyMock.createMock(WPBPage.class);
	projectMock = EasyMock.createMock(WPBProject.class);
	Whitebox.setInternalState(pageContentBuilder, "templateEngine", templateEngineMock);
}

@Test
public void test_initialize()
{
		
	try
	{
		templateEngineMock.initialize();
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock);	
		pageContentBuilder.initialize();
	}catch (Exception e)
	{
		assertTrue (false);
	}
	
	EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock);
}

@Test
public void test_find_web_page()
{
		
	try
	{
		String pageExternalKey = "123";
		EasyMock.expect(cacheInstancesMock.getPageCache()).andReturn(pagesCacheMock);
		EasyMock.expect(pagesCacheMock.getByExternalKey(pageExternalKey)).andReturn(pageMock);		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock);	
		WPBPage result = pageContentBuilder.findWebPage(pageExternalKey);
		assertTrue (result == pageMock);
		
	}catch (Exception e)
	{
		assertTrue (false);
	}
	
	EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock);
}


@Test
public void test_buildPageContent_null_isTemplateSource()
{
	
	try
	{
		InternalModel model = new InternalModel();
		String htmlSource = "<html>text</html>";
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(null);
		EasyMock.expect(pageMock.getHtmlSource()).andReturn(htmlSource);
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		assertTrue (result.equals(htmlSource));

	}catch (Exception e)
	{
		assertTrue (false);
	}	
}

@Test
public void test_buildPageContent_zero_isTemplateSource()
{
	
	try
	{
		InternalModel model = new InternalModel();
		String htmlSource = "<html>text</html>";
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(0);
		EasyMock.expect(pageMock.getHtmlSource()).andReturn(htmlSource);
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		assertTrue (result.equals(htmlSource));

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_zero_ok_nullController()
{
	
	try
	{
		InternalModel model = new InternalModel();
		String externalKey = "index";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn(null);
		EasyMock.expect(pageMock.getExternalKey()).andReturn(externalKey);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_validController()
{
	
	try
	{
		InternalModel model = new InternalModel();
		Map<String, String> locale = new HashMap<String, String>();
		locale.put(WPBModel.LOCALE_COUNTRY_KEY, "");
		locale.put(WPBModel.LOCALE_LANGUAGE_KEY, "en");
		model.getCmsModel().put(WPBModel.LOCALE_KEY, locale);
		
		String externalKey = "index";
		String controllerClass = "com.webpagebytes.cms.engine.DummyPageModelProvider";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn(controllerClass);
		
		EasyMock.expect(pageMock.getExternalKey()).andReturn(externalKey);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_zero_ok_emptyController()
{
	
	try
	{
		InternalModel model = new InternalModel();
		String externalKey = "index";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn("");
		EasyMock.expect(pageMock.getExternalKey()).andReturn(externalKey);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
		EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);		
		

	}catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_buildPageContent_templateException()
{
	
	try
	{
		InternalModel model = new InternalModel();
		String externalKey = "index";
		
		EasyMock.expect(pageMock.getIsTemplateSource()).andReturn(1);

		modelBuilderMock.populateModelForWebPage(pageMock, model);
		
		EasyMock.expect(pageMock.getPageModelProvider()).andReturn(null);
		EasyMock.expect(pageMock.getExternalKey()).andReturn(externalKey);
		
		templateEngineMock.process(EasyMock.anyObject(String.class), EasyMock.anyObject(Map.class), EasyMock.anyObject(Writer.class));
		EasyMock.expectLastCall().andThrow(new WPBTemplateException(""));
		EasyMock.replay(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);	
		String result = pageContentBuilder.buildPageContent(requestMock, pageMock, model);
				
	}
	catch (WPBTemplateException e)
	{
		// OK
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	EasyMock.verify(cacheInstancesMock, pagesCacheMock, modelBuilderMock, templateEngineMock, pageMock, projectMock, requestMock);
}

@Test
public void test_getPageModelProvider_exists_in_map()
{
	String controllerClass = "com.webpagebytes.cms.DefaultPageModelProvider";
	Map<String, WPBPageModelProvider> customControllers = new HashMap<String, WPBPageModelProvider>();
	WPBPageModelProvider instController = new DummyPageModelProvider();
	customControllers.put(controllerClass, instController);	
	Whitebox.setInternalState(pageContentBuilder, "customControllers", customControllers);
	
	try
	{
		WPBPageModelProvider result = Whitebox.invokeMethod(pageContentBuilder, "getPageModelProvider", controllerClass);
		
		assertTrue (result == instController);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_getPageModelProvider_not_exists_in_map()
{
	String controllerClass = "com.webpagebytes.cms.engine.DummyPageModelProvider";
	try
	{
		WPBPageModelProvider result = Whitebox.invokeMethod(pageContentBuilder, "getPageModelProvider", controllerClass);		
		assertTrue (result != null);
		
		Map<String, WPBPageModelProvider> controllers = Whitebox.getInternalState(pageContentBuilder, "customControllers");
		assertTrue (controllers.get(controllerClass) != null);
	} catch (Exception e)
	{
		assertTrue (false);
	}	
}


@Test
public void test_getPageModelProvider_exception()
{
	String controllerClass = "com.webpagebytes.cms.DoesNotExistsModelProvider";	
	try
	{
		Whitebox.invokeMethod(pageContentBuilder, "getPageModelProvider", controllerClass);		
	} 
	catch (WPBException e)
	{
		// OK
	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	
}

}
