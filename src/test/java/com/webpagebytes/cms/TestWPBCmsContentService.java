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

import com.webpagebytes.cms.appinterfaces.WPBContentProvider;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBProjectCache;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.exception.WPBLocaleException;
import com.webpagebytes.cms.utility.Pair;

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
	        	WPBContentProvider mockProvider = EasyMock.createMock(WPBContentProvider.class);
	    		Whitebox.setInternalState(contentService, "contentProvider", mockProvider);
	        	return null;
	        } }).when(contentService, "initializeContentProvider");
		WPBContentProvider result = contentService.getContentProvider();
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
      	WPBContentProvider mockProvider = EasyMock.createMock(WPBContentProvider.class);
	    Whitebox.setInternalState(contentService, "contentProvider", mockProvider);
	    WPBContentProvider result = contentService.getContentProvider();
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
		PowerMockito.doReturn(pageContentBuilderMock).when(contentService, "createPageContentBuilder", Matchers.any(WPBCacheInstances.class), Matchers.any(ModelBuilder.class) );
	
		FileContentBuilder fileContentBuilderMock = EasyMock.createMock(FileContentBuilder.class);
		PowerMockito.doReturn(fileContentBuilderMock).when(contentService, "createFileContentBuilder", Matchers.any(WPBCacheInstances.class));
	
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
		WPBCacheInstances cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WPBProjectCache projectCacheMock = EasyMock.createMock(WPBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(new Pair<String, String>("en", "GB"));
		Capture<String> captureLanguage = new Capture<String>();
		Capture<String> captureCountry = new Capture<String>();
		Capture<WPBModel> captureModel1 = new Capture<WPBModel>();		
		modelBuilderMock.populateLocale(EasyMock.capture(captureLanguage), EasyMock.capture(captureCountry), EasyMock.capture(captureModel1));
		
		Capture<WPBModel> captureModel2 = new Capture<WPBModel>();		
		modelBuilderMock.populateGlobalParameters(EasyMock.capture(captureModel2));
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		WPBModel model = contentService.createModel();
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
		WPBCacheInstances cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WPBProjectCache projectCacheMock = EasyMock.createMock(WPBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
	
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andThrow(new WPBIOException(""));
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		contentService.createModel();
		
		assertTrue(false);
		
	}
	catch (WPBException e)
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
		WPBCacheInstances cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WPBProjectCache projectCacheMock = EasyMock.createMock(WPBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		Set<String> supportedLocales = new HashSet<String>();
		supportedLocales.add("en_GB");
		supportedLocales.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLocales);
		Capture<String> captureLanguage = new Capture<String>();
		Capture<String> captureCountry = new Capture<String>();
		Capture<WPBModel> captureModel1 = new Capture<WPBModel>();		
		modelBuilderMock.populateLocale(EasyMock.capture(captureLanguage), EasyMock.capture(captureCountry), EasyMock.capture(captureModel1));
		
		Capture<WPBModel> captureModel2 = new Capture<WPBModel>();		
		modelBuilderMock.populateGlobalParameters(EasyMock.capture(captureModel2));
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		WPBModel model = contentService.createModel(language, country);
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
		WPBCacheInstances cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
		Whitebox.setInternalState(contentService, "cacheInstances", cacheInstancesMock);
		WPBProjectCache projectCacheMock = EasyMock.createMock(WPBProjectCache.class);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		Set<String> supportedLocales = new HashSet<String>();
		supportedLocales.add("en_GB");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLocales);
		
		EasyMock.replay(modelBuilderMock, cacheInstancesMock, projectCacheMock);
		contentService.createModel("fr", "CA");
		assertTrue(false);
	
		
	} 
	catch (WPBException e)
	{
		assertTrue( e instanceof WPBLocaleException);
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

}
