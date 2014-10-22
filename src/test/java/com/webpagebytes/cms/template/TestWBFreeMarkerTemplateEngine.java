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

import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBMessagesCache;
import com.webpagebytes.cms.datautility.WBCloudFileStorage;
import com.webpagebytes.cms.datautility.WBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.template.WBFreeMarkerArticleDirective;
import com.webpagebytes.cms.template.WBFreeMarkerFactory;
import com.webpagebytes.cms.template.WBFreeMarkerImageDirective;
import com.webpagebytes.cms.template.WBFreeMarkerModuleDirective;
import com.webpagebytes.cms.template.WBFreeMarkerTemplateEngine;
import com.webpagebytes.cms.template.WBFreeMarkerTemplateLoader;
import com.webpagebytes.cms.template.WBResourceBundle;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerTemplateEngine {

private WBCacheFactory cacheFactoryMock;
private WBFreeMarkerFactory freeMarkerFactoryMock;
private Configuration configurationMock;
private WBFreeMarkerTemplateLoader templateLoaderMock;
private WBFreeMarkerModuleDirective moduleDirectiveMock;
private WBFreeMarkerImageDirective imageDirectiveMock;
private WBFreeMarkerArticleDirective articleDirectiveMock;
private WBMessagesCache messageCacheMock;
private WBCacheInstances cacheInstancesMock;
private WBCloudFileStorage cloudStorageMock;
private WBCloudFileStorage cloudFileStorageMock;

@Before
public void setUp()
{
	cloudFileStorageMock = EasyMock.createMock(WBCloudFileStorage.class);
	Whitebox.setInternalState(WBCloudFileStorageFactory.class, "instance", cloudFileStorageMock);

	cacheFactoryMock = PowerMock.createMock(WBCacheFactory.class);
	freeMarkerFactoryMock = PowerMock.createMock(WBFreeMarkerFactory.class);
	configurationMock = PowerMock.createMock(Configuration.class);
	templateLoaderMock = PowerMock.createMock(WBFreeMarkerTemplateLoader.class);
	moduleDirectiveMock = PowerMock.createMock(WBFreeMarkerModuleDirective.class);
	imageDirectiveMock = PowerMock.createMock(WBFreeMarkerImageDirective.class);
	articleDirectiveMock = PowerMock.createMock(WBFreeMarkerArticleDirective.class);
	cloudStorageMock = PowerMock.createMock(WBCloudFileStorage.class);
	messageCacheMock = PowerMock.createMock(WBMessagesCache.class);
	cacheInstancesMock = PowerMock.createMock(WBCacheInstances.class);
	
	Logger loggerMock = PowerMock.createMock(Logger.class);
	Whitebox.setInternalState(WBFreeMarkerTemplateEngine.class, loggerMock);
}

@After
public void tearDown()
{
	Whitebox.setInternalState(WBCloudFileStorageFactory.class, "instance", (WBCloudFileStorage)null);
}


@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WBFreeMarkerTemplateEngine.class})
public void test_initialize()
{
	WBFreeMarkerTemplateEngine templateEngine = new WBFreeMarkerTemplateEngine(cacheInstancesMock);

	EasyMock.expect(freeMarkerFactoryMock.createConfiguration()).andReturn(configurationMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerModuleDirective()).andReturn(moduleDirectiveMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerTemplateLoader(cacheInstancesMock)).andReturn(templateLoaderMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerImageDirective()).andReturn(imageDirectiveMock);
	EasyMock.expect(freeMarkerFactoryMock.createWBFreeMarkerArticleDirective()).andReturn(articleDirectiveMock);
	
	configurationMock.setLocalizedLookup(false);
	configurationMock.setTemplateLoader(templateLoaderMock);
	moduleDirectiveMock.initialize(templateEngine, cacheInstancesMock);
	imageDirectiveMock.initialize(cloudStorageMock, cacheInstancesMock);
	configurationMock.setSharedVariable(WBModel.MODULE_DIRECTIVE, moduleDirectiveMock);
	configurationMock.setSharedVariable(WBModel.IMAGE_DIRECTIVE, imageDirectiveMock);
	configurationMock.setSharedVariable(WBModel.ARTICLE_DIRECTIVE, articleDirectiveMock);
	
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
@PrepareForTest({Environment.class, WBFreeMarkerTemplateEngine.class})
public void process_ok_no_messages()
{
	try
	{
		WBFreeMarkerTemplateEngine templateEngine = new WBFreeMarkerTemplateEngine(cacheInstancesMock);
		Whitebox.setInternalState(templateEngine, "configuration", configurationMock);
		String nameTemplate = "textXYZ";
		Map rootMap = new HashMap();
		rootMap.put(WBModel.LOCALE_LANGUAGE_KEY, "en");
		Writer out = new StringWriter();
		
		Template templateMock = PowerMock.createMock(Template.class);
		EasyMock.expect(configurationMock.getTemplate(nameTemplate)).andReturn(templateMock);
		
		Locale locale = new Locale("en");
		WBResourceBundle resourceBundleMock = PowerMock.createMock(WBResourceBundle.class);
		EasyMock.expect(freeMarkerFactoryMock.createResourceBundle(EasyMock.anyObject(WBMessagesCache.class), EasyMock.anyObject(Locale.class))).andReturn(resourceBundleMock);
		
		Environment envMock = PowerMock.createMock(Environment.class);
		EasyMock.expect(templateMock.createProcessingEnvironment(rootMap, out)).andReturn(envMock);
			
		envMock.process();
		Whitebox.setInternalState(templateEngine, "wbFreeMarkerFactory", freeMarkerFactoryMock);
		
		PowerMock.replay(cloudFileStorageMock, envMock, templateMock, resourceBundleMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
	
		templateEngine.process(nameTemplate, rootMap, out);
		
		PowerMock.verify(cloudFileStorageMock, envMock, templateMock, resourceBundleMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);

		assertTrue (rootMap.containsKey(WBModel.LOCALE_MESSAGES));
	} catch (Exception e)
	{
		assertTrue (false);
	}
	
}

@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WBFreeMarkerTemplateEngine.class})
public void process_ok_with_messages()
{
	try
	{
		WBFreeMarkerTemplateEngine templateEngine = new WBFreeMarkerTemplateEngine(cacheInstancesMock);
		Whitebox.setInternalState(templateEngine, "configuration", configurationMock);
		String nameTemplate = "textXYZ";
		Map rootMap = new HashMap();
		rootMap.put(WBModel.LOCALE_LANGUAGE_KEY, "en");
		rootMap.put(WBModel.LOCALE_MESSAGES, new Object());
		
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

		assertTrue (rootMap.containsKey(WBModel.LOCALE_MESSAGES));
	} catch (Exception e)
	{
		assertTrue (false);
	}
	
}

@Test
@SuppressStaticInitializationFor("WBFreeMarkerTemplateEngine.class")
@PrepareForTest({Environment.class, WBFreeMarkerTemplateEngine.class})
public void process_exception()
{
	WBFreeMarkerTemplateEngine templateEngine = new WBFreeMarkerTemplateEngine(cacheInstancesMock);
	Template templateMock = PowerMock.createMock(Template.class);
	WBResourceBundle resourceBundleMock = PowerMock.createMock(WBResourceBundle.class);
	Environment envMock = PowerMock.createMock(Environment.class);
	Map rootMap = new HashMap();
	
	try
	{
		Whitebox.setInternalState(templateEngine, "configuration", configurationMock);
		String nameTemplate = "textXYZ";
		rootMap.put(WBModel.LOCALE_LANGUAGE_KEY, "en");
		Writer out = new StringWriter();		
		EasyMock.expect(configurationMock.getTemplate(nameTemplate)).andReturn(templateMock);		
		Locale locale = new Locale("en");
		EasyMock.expect(freeMarkerFactoryMock.createResourceBundle(EasyMock.anyObject(WBMessagesCache.class), EasyMock.anyObject(Locale.class))).andReturn(resourceBundleMock);		
		EasyMock.expect(templateMock.createProcessingEnvironment(rootMap, out)).andReturn(envMock);			
		Whitebox.setInternalState(templateEngine, "wbFreeMarkerFactory", freeMarkerFactoryMock);

		envMock.process();
		EasyMock.expectLastCall().andThrow(new IOException());
		
		PowerMock.replay(cloudFileStorageMock, envMock, templateMock, resourceBundleMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
	
		templateEngine.process(nameTemplate, rootMap, out);
		
		assertTrue (false);
	} 
	catch (WBIOException e)
	{
		PowerMock.verify(cloudFileStorageMock, envMock, templateMock, cacheFactoryMock, freeMarkerFactoryMock, configurationMock, templateLoaderMock, moduleDirectiveMock, messageCacheMock);
		assertTrue (rootMap.containsKey(WBModel.LOCALE_LANGUAGE_KEY));

	}
	catch (Exception e)
	{
		assertTrue (false);
	}
	
}

}
