package com.webpagebytes.cms.template;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

import org.junit.Test;

import com.webpagebytes.cms.appinterfaces.WPBCacheFactory;
import com.webpagebytes.cms.appinterfaces.WPBFileStorage;
import com.webpagebytes.cms.appinterfaces.WPBMessagesCache;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.template.FreeMarkerArticleDirective;
import com.webpagebytes.cms.template.FreeMarkerResourcesFactory;
import com.webpagebytes.cms.template.FreeMarkerImageDirective;
import com.webpagebytes.cms.template.FreeMarkerModuleDirective;
import com.webpagebytes.cms.template.WPBFreeMarkerTemplateEngine;
import com.webpagebytes.cms.template.FreeMarkerTemplateLoader;
import com.webpagebytes.cms.template.CmsResourceBundle;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerTemplateEngine {

private WPBCacheFactory cacheFactoryMock;
private FreeMarkerResourcesFactory freeMarkerFactoryMock;
private Configuration configurationMock;
private FreeMarkerTemplateLoader templateLoaderMock;
private FreeMarkerModuleDirective moduleDirectiveMock;
private FreeMarkerImageDirective imageDirectiveMock;
private FreeMarkerArticleDirective articleDirectiveMock;
private WPBMessagesCache messageCacheMock;
private WPBCacheInstances cacheInstancesMock;
private WPBFileStorage cloudStorageMock;
private WPBFileStorage cloudFileStorageMock;

@Before
public void setUp()
{
	cloudFileStorageMock = EasyMock.createMock(WPBFileStorage.class);
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", cloudFileStorageMock);

	cacheFactoryMock = PowerMock.createMock(WPBCacheFactory.class);
	freeMarkerFactoryMock = PowerMock.createMock(FreeMarkerResourcesFactory.class);
	configurationMock = PowerMock.createMock(Configuration.class);
	templateLoaderMock = PowerMock.createMock(FreeMarkerTemplateLoader.class);
	moduleDirectiveMock = PowerMock.createMock(FreeMarkerModuleDirective.class);
	imageDirectiveMock = PowerMock.createMock(FreeMarkerImageDirective.class);
	articleDirectiveMock = PowerMock.createMock(FreeMarkerArticleDirective.class);
	cloudStorageMock = PowerMock.createMock(WPBFileStorage.class);
	messageCacheMock = PowerMock.createMock(WPBMessagesCache.class);
	cacheInstancesMock = PowerMock.createMock(WPBCacheInstances.class);
	
	Logger loggerMock = PowerMock.createMock(Logger.class);
	Whitebox.setInternalState(WPBFreeMarkerTemplateEngine.class, loggerMock);
}

@After
public void tearDown()
{
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", (WPBFileStorage)null);
}


@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WPBFreeMarkerTemplateEngine.class})
public void test_initialize()
{
	WPBFreeMarkerTemplateEngine templateEngine = new WPBFreeMarkerTemplateEngine(cacheInstancesMock);

	EasyMock.expect(freeMarkerFactoryMock.createConfiguration()).andReturn(configurationMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerModuleDirective()).andReturn(moduleDirectiveMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerTemplateLoader(cacheInstancesMock)).andReturn(templateLoaderMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerImageDirective()).andReturn(imageDirectiveMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerArticleDirective()).andReturn(articleDirectiveMock);
	
	configurationMock.setLocalizedLookup(false);
	configurationMock.setTemplateLoader(templateLoaderMock);
	moduleDirectiveMock.initialize(templateEngine, cacheInstancesMock);
	imageDirectiveMock.initialize(cloudStorageMock, cacheInstancesMock);
	configurationMock.setSharedVariable(WPBModel.MODULE_DIRECTIVE, moduleDirectiveMock);
	configurationMock.setSharedVariable(WPBModel.IMAGE_DIRECTIVE, imageDirectiveMock);
	configurationMock.setSharedVariable(WPBModel.ARTICLE_DIRECTIVE, articleDirectiveMock);
	
	Capture<String> captureDefaultEncoding = new Capture<String>();
	Capture<String> captureOutputEncoding = new Capture<String>();	
	configurationMock.setDefaultEncoding(EasyMock.capture(captureDefaultEncoding));
	configurationMock.setOutputEncoding(EasyMock.capture(captureOutputEncoding));
	
	Whitebox.setInternalState(templateEngine, "wbFreeMarkerFactory", freeMarkerFactoryMock);
		
	PowerMock.replay(cloudFileStorageMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);	
	try
	{
		templateEngine.initialize();
		
		PowerMock.verify(cloudFileStorageMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);

		assertTrue (captureDefaultEncoding.getValue().equals("UTF-8"));
		assertTrue (captureOutputEncoding.getValue().equals("UTF-8"));

	} catch (Exception e)
	{
		assertTrue (false);
	}
	
}

@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WPBFreeMarkerTemplateEngine.class})
public void process_ok_no_messages()
{
	try
	{
		WPBFreeMarkerTemplateEngine templateEngine = new WPBFreeMarkerTemplateEngine(cacheInstancesMock);
		Whitebox.setInternalState(templateEngine, "configuration", configurationMock);
		String nameTemplate = "textXYZ";
		Map rootMap = new HashMap();
		rootMap.put(WPBModel.LOCALE_LANGUAGE_KEY, "en");
		Writer out = new StringWriter();
		
		Template templateMock = PowerMock.createMock(Template.class);
		EasyMock.expect(configurationMock.getTemplate(nameTemplate)).andReturn(templateMock);
		
		Locale locale = new Locale("en");
		CmsResourceBundle resourceBundleMock = PowerMock.createMock(CmsResourceBundle.class);
		EasyMock.expect(freeMarkerFactoryMock.createResourceBundle(EasyMock.anyObject(WPBMessagesCache.class), EasyMock.anyObject(Locale.class))).andReturn(resourceBundleMock);
		
		Environment envMock = PowerMock.createMock(Environment.class);
		EasyMock.expect(templateMock.createProcessingEnvironment(rootMap, out)).andReturn(envMock);
			
		envMock.process();
		Whitebox.setInternalState(templateEngine, "wbFreeMarkerFactory", freeMarkerFactoryMock);
		
		PowerMock.replay(cloudFileStorageMock, envMock, templateMock, resourceBundleMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
	
		templateEngine.process(nameTemplate, rootMap, out);
		
		PowerMock.verify(cloudFileStorageMock, envMock, templateMock, resourceBundleMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);

		assertTrue (rootMap.containsKey(WPBModel.LOCALE_MESSAGES));
	} catch (Exception e)
	{
		assertTrue (false);
	}
	
}

@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WPBFreeMarkerTemplateEngine.class})
public void process_ok_with_messages()
{
	try
	{
		WPBFreeMarkerTemplateEngine templateEngine = new WPBFreeMarkerTemplateEngine(cacheInstancesMock);
		Whitebox.setInternalState(templateEngine, "configuration", configurationMock);
		String nameTemplate = "textXYZ";
		Map rootMap = new HashMap();
		rootMap.put(WPBModel.LOCALE_LANGUAGE_KEY, "en");
		rootMap.put(WPBModel.LOCALE_MESSAGES, new Object());
		
		Writer out = new StringWriter();
		
		Template templateMock = PowerMock.createMock(Template.class);
		EasyMock.expect(configurationMock.getTemplate(nameTemplate)).andReturn(templateMock);
		
		
		Environment envMock = PowerMock.createMock(Environment.class);
		EasyMock.expect(templateMock.createProcessingEnvironment(rootMap, out)).andReturn(envMock);
			
		envMock.process();
		Whitebox.setInternalState(templateEngine, "wbFreeMarkerFactory", freeMarkerFactoryMock);
		
		PowerMock.replay(cloudFileStorageMock, envMock, templateMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
	
		templateEngine.process(nameTemplate, rootMap, out);
		
		PowerMock.verify(cloudFileStorageMock, envMock, templateMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);

		assertTrue (rootMap.containsKey(WPBModel.LOCALE_MESSAGES));
	} catch (Exception e)
	{
		assertTrue (false);
	}
	
}

@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WPBFreeMarkerTemplateEngine.class})
public void process_exception()
{
	WPBFreeMarkerTemplateEngine templateEngine = new WPBFreeMarkerTemplateEngine(cacheInstancesMock);
	Template templateMock = PowerMock.createMock(Template.class);
	CmsResourceBundle resourceBundleMock = PowerMock.createMock(CmsResourceBundle.class);
	Environment envMock = PowerMock.createMock(Environment.class);
	Map rootMap = new HashMap();
	
	try
	{
		Whitebox.setInternalState(templateEngine, "configuration", configurationMock);
		String nameTemplate = "textXYZ";
		rootMap.put(WPBModel.LOCALE_LANGUAGE_KEY, "en");
		Writer out = new StringWriter();		
		EasyMock.expect(configurationMock.getTemplate(nameTemplate)).andReturn(templateMock);		
		Locale locale = new Locale("en");
		EasyMock.expect(freeMarkerFactoryMock.createResourceBundle(EasyMock.anyObject(WPBMessagesCache.class), EasyMock.anyObject(Locale.class))).andReturn(resourceBundleMock);		
		EasyMock.expect(templateMock.createProcessingEnvironment(rootMap, out)).andReturn(envMock);			
		Whitebox.setInternalState(templateEngine, "wbFreeMarkerFactory", freeMarkerFactoryMock);

		envMock.process();
		EasyMock.expectLastCall().andThrow(new IOException());
		
		PowerMock.replay(cloudFileStorageMock, envMock, templateMock, resourceBundleMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
	
		templateEngine.process(nameTemplate, rootMap, out);
		
		assertTrue (false);
	} 
	catch (WPBIOException e)
	{
		PowerMock.verify(cloudFileStorageMock, envMock, templateMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
		assertTrue (rootMap.containsKey(WPBModel.LOCALE_LANGUAGE_KEY));

	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	
}

}
