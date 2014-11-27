package com.webpagebytes.cms.template;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

import org.junit.Test;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.template.FreeMarkerModuleDirective;
import com.webpagebytes.cms.template.WPBTemplateEngine;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("WBFreeMarkerModuleDirective.class")
@PrepareForTest({Environment.class, FreeMarkerModuleDirective.class})
public class TestWBFreeMarkerModuleDirective {

private WPBCacheInstances cacheInstancesMock;
private WPBTemplateEngine templateEngineMock;

public static void copyParams(Environment env, Map params) throws TemplateModelException
{
	
}

@Before
public void setUp()
{
	cacheInstancesMock = PowerMock.createMock(WPBCacheInstances.class);
	templateEngineMock = PowerMock.createMock(WPBTemplateEngine.class);
	Logger loggerMock = PowerMock.createMock(Logger.class);
	Whitebox.setInternalState(FreeMarkerModuleDirective.class, loggerMock);	
}

@Test
public void test_initialize()
{
	EasyMock.replay(cacheInstancesMock, templateEngineMock);
	
	FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
	templateDirective.initialize(templateEngineMock, cacheInstancesMock);
	
	assertTrue (Whitebox.getInternalState(templateDirective,"templateEngine") == templateEngineMock);
	assertTrue (Whitebox.getInternalState(templateDirective,"cacheInstances") == cacheInstancesMock);
	EasyMock.verify(cacheInstancesMock, templateEngineMock);
	
}

@Test
public void test_execute_directiveBodyException()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = PowerMock.createMock(TemplateDirectiveBody.class);
	Map params = new HashMap();
	
	EasyMock.replay(cacheInstancesMock, templateEngineMock, envMock, directiveBodyMock);
	
	FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
	try
	{
		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);
		// should not get here
		assertTrue(false);
		
	} catch (Exception e)
	{
		assertTrue( e instanceof TemplateModelException);
	}
	EasyMock.verify(cacheInstancesMock, templateEngineMock, envMock, directiveBodyMock);

}

/*
@Test
public void test_execute_plainhtml()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = null;
	
	Map params = new HashMap();
	String name = "testXYZ";
	StringModel nameModel = new StringModel(name, new DefaultObjectWrapper() );
	params.put("name", nameModel);

	String htmlSource = "<b>this is bold text</b> and this is not bold";
	try
	{
		TemplateHashModel dataModelMock = PowerMock.createMock(TemplateHashModel.class);
		TemplateModel wbMessagesMock = PowerMock.createMock(TemplateModel.class);
		EasyMock.expect(envMock.getDataModel()).andReturn(dataModelMock);
		EasyMock.expect(dataModelMock.get(PageContentBuilder.LOCALE_MESSAGES)).andReturn(wbMessagesMock);
		
		TemplateModel pageParamsMock = PowerMock.createMock(TemplateModel.class);
		EasyMock.expect(dataModelMock.get(PageContentBuilder.PAGE_PARAMETERS_KEY)).andReturn(pageParamsMock);

		TemplateModel urlParamsMock = PowerMock.createMock(TemplateModel.class);
		EasyMock.expect(dataModelMock.get(PageContentBuilder.URL_PARAMETERS_KEY)).andReturn(pageParamsMock);

		WBWebPageModule pageModuleMock = PowerMock.createMock(WBWebPageModule.class);		
		WBWebPageModuleCache pageModuleCacheMock = PowerMock.createMock(WBWebPageModuleCache.class);
		EasyMock.expect(pageModuleCacheMock.get(name)).andReturn(pageModuleMock);
		EasyMock.expect(pageModuleMock.getIsTemplateSource()).andReturn(0);
		EasyMock.expect(pageModuleMock.getHtmlSource()).andReturn(htmlSource);
		
		StringWriter outWriter = new StringWriter();
		EasyMock.expect(envMock.getOut()).andReturn(outWriter);
		
		EasyMock.replay(cacheFactoryMock, templateEngineMock, envMock, dataModelMock, pageModuleMock, pageModuleCacheMock);
		
		WBFreeMarkerModuleDirective templateDirective = new WBFreeMarkerModuleDirective();
		templateDirective.setPageModuleCache(pageModuleCacheMock);
		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);
		
		EasyMock.verify(cacheFactoryMock, templateEngineMock, envMock, dataModelMock, pageModuleMock, pageModuleCacheMock);
		
		assertTrue (params.containsKey(PageContentBuilder.LOCALE_MESSAGES));
		assertTrue( outWriter.getBuffer().toString().equals(htmlSource));
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}
*/

@Test
public void test_execute_plainhtml()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = null;
	
	Map params = new HashMap();
	String key = "testXYZ";
	StringModel keyModel = new StringModel(key, new DefaultObjectWrapper() );
	params.put("externalKey", keyModel);

	String htmlSource = "<b>this is bold text</b> and this is not bold";
	try
	{

		
		WPBWebPageModule pageModuleMock = PowerMock.createMock(WPBWebPageModule.class);		
		WPBWebPageModulesCache pageModuleCacheMock = PowerMock.createMock(WPBWebPageModulesCache.class);
		EasyMock.expect(pageModuleCacheMock.getByExternalKey(key)).andReturn(pageModuleMock);
		EasyMock.expect(pageModuleMock.getIsTemplateSource()).andReturn(0);
		EasyMock.expect(pageModuleMock.getHtmlSource()).andReturn(htmlSource);
		EasyMock.expect(cacheInstancesMock.getWBWebPageModuleCache()).andReturn(pageModuleCacheMock);
		
		StringWriter outWriter = new StringWriter();
		EasyMock.expect(envMock.getOut()).andReturn(outWriter);
		
		EasyMock.replay(cacheInstancesMock, templateEngineMock, envMock, pageModuleMock, pageModuleCacheMock);
		
		FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
		
		PowerMock.suppressMethod(FreeMarkerModuleDirective.class, "copyParams");
		
		Whitebox.setInternalState(templateDirective, "cacheInstances", cacheInstancesMock);
		
		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);
		
		EasyMock.verify(cacheInstancesMock, templateEngineMock, envMock, pageModuleMock, pageModuleCacheMock);
		
		assertTrue( outWriter.getBuffer().toString().equals(htmlSource));
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_execute_templathtml()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = null;
	
	Map params = new HashMap();
	String key = "test123";
	StringModel keyModel = new StringModel(key, new DefaultObjectWrapper() );
	params.put("externalKey", keyModel);
	String name = "name123";
	String htmlSource = "<b>this is bold ${x}</b> and this is not bold ${x}";
	try
	{
	
		WPBWebPageModule pageModuleMock = PowerMock.createMock(WPBWebPageModule.class);
		EasyMock.expect(pageModuleMock.getName()).andReturn(name);
		WPBWebPageModulesCache pageModuleCacheMock = PowerMock.createMock(WPBWebPageModulesCache.class);
		EasyMock.expect(pageModuleCacheMock.getByExternalKey(key)).andReturn(pageModuleMock);
		EasyMock.expect(pageModuleMock.getIsTemplateSource()).andReturn(1);

		EasyMock.expect(cacheInstancesMock.getWBWebPageModuleCache()).andReturn(pageModuleCacheMock);

		StringWriter outWriter = new StringWriter();
		EasyMock.expect(envMock.getOut()).andReturn(outWriter);
	
		Capture<String> captureTemplateName = new Capture<String>();
		Capture<Map> captureRoot = new Capture<Map>();
		Capture<Writer> captureWriter = new Capture<Writer>();
		
		templateEngineMock.process(EasyMock.capture(captureTemplateName), EasyMock.capture(captureRoot), EasyMock.capture(captureWriter));	
		EasyMock.replay(cacheInstancesMock, templateEngineMock, envMock, pageModuleMock, pageModuleCacheMock);
		
		FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
		Whitebox.setInternalState(templateDirective, "templateEngine",templateEngineMock);
		Whitebox.setInternalState(templateDirective, "cacheInstances",cacheInstancesMock);
		PowerMock.suppressMethod(FreeMarkerModuleDirective.class, "copyParams");
		
		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);
		
		EasyMock.verify(cacheInstancesMock, templateEngineMock, envMock, pageModuleMock, pageModuleCacheMock);
		assertTrue(captureRoot.getValue() == params);
		assertTrue(captureTemplateName.getValue().equals(WPBTemplateEngine.WEBMODULES_PATH_PREFIX + name));
		assertTrue(captureWriter.getValue() == outWriter);

	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_execute_catch_exception()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = null;
	
	Map params = new HashMap();
	String name = "testXYZ";
	StringModel nameModel = new StringModel(name, new DefaultObjectWrapper() );
	params.put("name", nameModel);

	try
	{
		
		WPBWebPageModule pageModuleMock = PowerMock.createMock(WPBWebPageModule.class);		
		WPBWebPageModulesCache pageModuleCacheMock = PowerMock.createMock(WPBWebPageModulesCache.class);
		EasyMock.expect(pageModuleCacheMock.get(name)).andThrow(new WPBIOException(""));
		
		EasyMock.expect(cacheInstancesMock.getWBWebPageModuleCache()).andReturn(pageModuleCacheMock);

		EasyMock.replay(cacheInstancesMock, templateEngineMock, envMock, pageModuleMock, pageModuleCacheMock);
		
		FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
		Whitebox.setInternalState(templateDirective, "templateEngine",templateEngineMock);
		Whitebox.setInternalState(templateDirective, "cacheInstances",cacheInstancesMock);
		PowerMock.suppressMethod(FreeMarkerModuleDirective.class, "copyParams");
		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);
		
		assertTrue(false);		
	} catch (Exception e)
	{
		assertTrue(e instanceof TemplateModelException);
	}
}

@Test
public void test_execute_noPageModule()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = null;
	
	Map params = new HashMap();
	String name = "testXYZ";
	StringModel nameModel = new StringModel(name, new DefaultObjectWrapper() );
	params.put("name", nameModel);

	try
	{

		WPBWebPageModule pageModuleMock = PowerMock.createMock(WPBWebPageModule.class);		
		WPBWebPageModulesCache pageModuleCacheMock = PowerMock.createMock(WPBWebPageModulesCache.class);
		EasyMock.expect(pageModuleCacheMock.get(name)).andReturn(null);
		
		EasyMock.expect(cacheInstancesMock.getWBWebPageModuleCache()).andReturn(pageModuleCacheMock);

		EasyMock.replay(cacheInstancesMock, templateEngineMock, envMock, pageModuleMock, pageModuleCacheMock);
		
		FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
		Whitebox.setInternalState(templateDirective, "templateEngine",templateEngineMock);
		Whitebox.setInternalState(templateDirective, "cacheInstances",cacheInstancesMock);
		PowerMock.suppressMethod(FreeMarkerModuleDirective.class, "copyParams");
		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);
	
		assertTrue(false);
		
	} catch (Exception e)
	{
		assertTrue(e instanceof TemplateModelException);
	}
}

@Test
public void test_execute_noDirectiveName()
{
	Environment envMock = PowerMock.createMock(Environment.class);
	TemplateModel[] loopVars = null;
	TemplateDirectiveBody directiveBodyMock = null;
	Map params = new HashMap();
		
	FreeMarkerModuleDirective templateDirective = new FreeMarkerModuleDirective();
	try
	{

		Whitebox.setInternalState(templateDirective, "templateEngine",templateEngineMock);
		Whitebox.setInternalState(templateDirective, "cacheInstances",cacheInstancesMock);
		PowerMock.suppressMethod(FreeMarkerModuleDirective.class, "copyParams");

		EasyMock.replay(cacheInstancesMock, templateEngineMock, envMock);

		templateDirective.execute(envMock, params, loopVars, directiveBodyMock);

		assertTrue (false);
		
	} catch (Exception e)
	{
		assertTrue(e instanceof TemplateModelException);
	}
}

}
