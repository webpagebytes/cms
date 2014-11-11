package com.webpagebytes.cms;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.appinterfaces.WBContentProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBProjectCache;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.exception.WBLocaleException;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({WPBCmsContentService.class})
public class TestWPBCmsContentService {

private	WPBCmsContentService contentService;

@Before
public void setUp()
{
	PowerMock.suppress(PowerMock.method(WPBCmsContentService.class, "createCacheInstances"));
	PowerMock.suppress(PowerMock.method(WPBCmsContentService.class, "createModelBuilder"));
	PowerMock.suppress(PowerMock.method(WPBCmsContentService.class, "createCacheFactory"));
	contentService = PowerMockito.spy(new WPBCmsContentService());	
}

@Test
public void test_getContentProvider()
{
	try
	{
		PowerMockito.doAnswer(new Answer<Void>() {
	        public Void answer(InvocationOnMock invocation) {
	        	WPBCmsContentService contentService = (WPBCmsContentService)invocation.getMock();
	        	WBContentProvider mockProvider = EasyMock.createMock(WBContentProvider.class);
	    		Whitebox.setInternalState(contentService, "contentProvider", mockProvider);
	        	return null;
	        } }).when(contentService, "initializeContentProvider");
		WBContentProvider result = contentService.getContentProvider();
		assertTrue (result != null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getContentProvider_already_set()
{
	try
	{
      	WBContentProvider mockProvider = EasyMock.createMock(WBContentProvider.class);
	    Whitebox.setInternalState(contentService, "contentProvider", mockProvider);
	    WBContentProvider result = contentService.getContentProvider();
		assertTrue (result == mockProvider);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_initializeContentProvider()
{
	try
	{
		PageContentBuilder pageContentBuilderMock = EasyMock.createMock(PageContentBuilder.class);
		PowerMockito.doReturn(pageContentBuilderMock).when(contentService, "createPageContentBuilder", Matchers.any(WBCacheInstances.class), Matchers.any(ModelBuilder.class) );
	
		FileContentBuilder fileContentBuilderMock = EasyMock.createMock(FileContentBuilder.class);
		PowerMockito.doReturn(fileContentBuilderMock).when(contentService, "createFileContentBuilder", Matchers.any(WBCacheInstances.class));
	
		Whitebox.invokeMethod(contentService, "initializeContentProvider");
		
		assertTrue (null != Whitebox.getInternalState(contentService, "contentProvider"));
		
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_createModel()
{
	try
	{
		ModelBuilder modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
		Whitebox.setInternalState(contentService, "modelBuilder", modelBuilderMock);
		WBCacheInstances cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WBProjectCache projectCacheMock = EasyMock.createMock(WBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(new Pair<String, String>("en", "GB"));
		Capture<String> captureLanguage = new Capture<String>();
		Capture<String> captureCountry = new Capture<String>();
		Capture<WBModel> captureModel1 = new Capture<WBModel>();		
		modelBuilderMock.populateLocale(EasyMock.capture(captureLanguage), EasyMock.capture(captureCountry), EasyMock.capture(captureModel1));
		
		Capture<WBModel> captureModel2 = new Capture<WBModel>();		
		modelBuilderMock.populateGlobalParameters(EasyMock.capture(captureModel2));
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		WBModel model = contentService.createModel();
		assertTrue(model != null);
		assertTrue(captureLanguage.getValue().equals("en"));
		assertTrue(captureCountry.getValue().equals("GB"));
		assertTrue(captureModel1.getValue() != null);
		assertTrue(captureModel2.getValue() != null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_createModel_exception()
{
	try
	{
		ModelBuilder modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
		Whitebox.setInternalState(contentService, "modelBuilder", modelBuilderMock);
		WBCacheInstances cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WBProjectCache projectCacheMock = EasyMock.createMock(WBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
	
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andThrow(new WBIOException(""));
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		contentService.createModel();
		
		assertTrue(false);
		
	}
	catch (WBException e)
	{
		assertTrue(true);
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

public void createModel_param(String language, String country)
{
	try
	{
		ModelBuilder modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
		Whitebox.setInternalState(contentService, "modelBuilder", modelBuilderMock);
		WBCacheInstances cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WBProjectCache projectCacheMock = EasyMock.createMock(WBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		Set<String> supportedLocales = new HashSet<String>();
		supportedLocales.add("en_GB");
		supportedLocales.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLocales);
		Capture<String> captureLanguage = new Capture<String>();
		Capture<String> captureCountry = new Capture<String>();
		Capture<WBModel> captureModel1 = new Capture<WBModel>();		
		modelBuilderMock.populateLocale(EasyMock.capture(captureLanguage), EasyMock.capture(captureCountry), EasyMock.capture(captureModel1));
		
		Capture<WBModel> captureModel2 = new Capture<WBModel>();		
		modelBuilderMock.populateGlobalParameters(EasyMock.capture(captureModel2));
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		WBModel model = contentService.createModel(language, country);
		assertTrue(model != null);
		assertTrue(captureLanguage.getValue().equals(language.toLowerCase()));
		if (country != null)
		{
			assertTrue(captureCountry.getValue().equals(country.toUpperCase()));
		}
		assertTrue(captureModel1.getValue() != null);
		assertTrue(captureModel2.getValue() != null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_createModel_param()
{
	createModel_param("en", "GB");
}
@Test
public void test_createModel_param_no_country()
{
	createModel_param("en", "");
}

@Test
public void test_createModel_param_null_country()
{
	createModel_param("en", null);
}

@Test
public void test_createModel_wrong_case()
{
	createModel_param("EN", "gb");
}

@Test
public void test_createModel_param_exception()
{
	try
	{
		ModelBuilder modelBuilderMock = EasyMock.createMock(ModelBuilder.class);
		Whitebox.setInternalState(contentService, "modelBuilder", modelBuilderMock);
		WBCacheInstances cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WBProjectCache projectCacheMock = EasyMock.createMock(WBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		Set<String> supportedLocales = new HashSet<String>();
		supportedLocales.add("en_GB");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLocales);
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		contentService.createModel("fr", "CA");
		assertTrue(false);
	
		
	} 
	catch (WBException e)
	{
		assertTrue( e instanceof WBLocaleException);
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

}
