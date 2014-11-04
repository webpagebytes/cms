package com.webpagebytes.cms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.ModelBuilder;
import com.webpagebytes.cms.Pair;
import com.webpagebytes.cms.PublicContentServlet;
import com.webpagebytes.cms.URLMatcherResult;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBParametersCache;
import com.webpagebytes.cms.cache.WBProjectCache;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBLocaleException;
import com.webpagebytes.cms.utility.WBConfiguration;
import com.webpagebytes.cms.utility.WBConfiguration.WPBSECTION;
import com.webpagebytes.cms.utility.WBConfigurationFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WBModel.class, ModelBuilder.class})
public class TestModelBuilder {
	
HttpServletRequest requestMock;
WBCacheInstances cacheInstancesMock;
ModelBuilder modelBuilder;
WBUri uriMock;
URLMatcherResult urlMatcherResultMock;
WBModel modelMock;
WBProjectCache projectCacheMock;
WBWebPage webPageMock;
WBConfiguration configurationMock;
Map<String, String> modelConfigs = new HashMap<String, String>();

@Before
public void setUp()
{
	configurationMock = EasyMock.createMock(WBConfiguration.class);
	Whitebox.setInternalState(WBConfigurationFactory.class, "configuration", configurationMock);
	EasyMock.expect(configurationMock.getSectionParams(WPBSECTION.SECTION_MODEL_CONFIGURATOR)).andReturn(modelConfigs);
	
	requestMock = EasyMock.createMock(HttpServletRequest.class);
	cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
	uriMock = EasyMock.createMock(WBUri.class);
	urlMatcherResultMock = EasyMock.createMock(URLMatcherResult.class);
	modelMock = EasyMock.createMock(WBModel.class);
	webPageMock = EasyMock.createMock(WBWebPage.class);
	projectCacheMock  = EasyMock.createMock(WBProjectCache.class);
}

@After
public void tearDown()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "configuration", (WBConfiguration) null);
}

@Test
public void test_populateModelForUriData()
{
	suppress(method(ModelBuilder.class, "populateUriParameters"));
	suppress(method(ModelBuilder.class, "populateGlobalParameters"));	
	suppress(method(ModelBuilder.class, "populateStaticParameters"));	
	try
	{
		EasyMock.expect(uriMock.getExternalKey()).andReturn("123");
		EasyMock.replay(requestMock, uriMock, urlMatcherResultMock, modelMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		modelBuilder.populateModelForUriData(requestMock, uriMock, urlMatcherResultMock, modelMock);		
		EasyMock.verify(requestMock, uriMock, urlMatcherResultMock, modelMock, configurationMock);
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateModelForWebPage()
{
	try
	{
		String pageExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "param1";
		String value1 = "value1";
		mapParams.put(param1, value1);
		
		WBParameter parameter = new WBParameter();
		parameter.setName(param1);
		parameter.setValue(value1);
		pageParams.add(parameter);
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);		
		EasyMock.expect(webPageMock.getExternalKey()).andReturn(pageExternalKey);		
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(paramsCacheMock.getAllForOwner("abc")).andReturn(pageParams);
		
		EasyMock.replay(requestMock, webPageMock, cacheInstancesMock, paramsCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		
		modelBuilder.populateModelForWebPage(webPageMock, model);		
		EasyMock.verify(requestMock, webPageMock, cacheInstancesMock, paramsCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WBModel.PAGE_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateUriParameters_OK_language_and_country()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "country";
		String value2 = "GB";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WBParameter parameter1 = new WBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WBParameter parameter2 = new WBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(1);
		parameter2.setLocaleType(WBParameter.PARAMETER_LOCALE_COUNTRY);
		pageParams.add(parameter2);		
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WBModel.URI_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateUriParameters_OK_only_language()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		mapParams.put(param1, value1);
		
		WBParameter parameter1 = new WBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WBModel.URI_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateUriParameters_empty_urlmatcher()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		Map<String, String> mapParams = new HashMap<String, String>();
		WBModel model = new WBModel();
		String param1 = "language";
		String value1 = "en";
		mapParams.put(param1, value1);
		
		WBParameter parameter1 = new WBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(0);
		parameter1.setLocaleType(WBParameter.PARAMETER_NO_TYPE);
		pageParams.add(parameter1);		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		
		assertTrue (model.getCmsModel().get(WBModel.URI_PARAMETERS_KEY).equals(mapParams));
		
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

/*
 * language is not supported
 */
@Test
public void test_populateUriParameters_language_not_supported()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "country";
		String value2 = "AU";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WBParameter parameter1 = new WBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WBParameter parameter2 = new WBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(1);
		parameter2.setLocaleType(WBParameter.PARAMETER_LOCALE_COUNTRY);
		pageParams.add(parameter2);		
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} 
		catch (WBLocaleException e)
		{
			// OK
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
				
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

/*
 * simulate the case where the url is /{xyz}/test.html and there is a {language} param that is overwrite from url and language identifier
 */
@Test
public void test_populateUriParameters_no_language_param()
{
	try
	{
		
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "xyz";
		String value2 = "";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WBParameter parameter1 = new WBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WBParameter parameter2 = new WBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(0);
		parameter2.setLocaleType(WBParameter.PARAMETER_NO_TYPE);
		pageParams.add(parameter2);		
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		// pattern params contains only the xyz param
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} 
		catch (WBLocaleException e)
		{
			// OK
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
			
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

/*
 * simulate the case where the url is /{xyz}/test.html and there is a {language} param that is overwrite from url and language identifier
 */
@Test
public void test_populateUriParameters_no_country_param()
{
	try
	{
		suppress(method(ModelBuilder.class, "populateLocale"));
		
		String uriExternalKey = "abc";
		List<WBParameter> pageParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "language";
		String value1 = "en";
		String param2 = "country";
		String value2 = "GB";
		mapParams.put(param1, value1);
		mapParams.put(param2, value2);
		
		WBParameter parameter1 = new WBParameter();
		parameter1.setName(param1);
		parameter1.setValue(value1);
		parameter1.setOverwriteFromUrl(1);
		parameter1.setLocaleType(WBParameter.PARAMETER_LOCALE_LANGUAGE);
		pageParams.add(parameter1);
		WBParameter parameter2 = new WBParameter();
		parameter2.setName(param2);
		parameter2.setValue(value2);
		parameter2.setOverwriteFromUrl(1);
		parameter2.setLocaleType(WBParameter.PARAMETER_LOCALE_COUNTRY);
		pageParams.add(parameter2);		
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);				
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(cacheInstancesMock.getProjectCache()).andReturn(projectCacheMock);
		
		Pair<String, String> defaultLocale = new Pair<String, String>("en", "");
		EasyMock.expect(projectCacheMock.getDefaultLocale()).andReturn(defaultLocale);
		
		Set<String> supportedLanguages = new HashSet<String>();
		supportedLanguages.add("en_GB");
		supportedLanguages.add("en");
		EasyMock.expect(projectCacheMock.getSupportedLocales()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		// pattern params contains only the language param
		patternParams.put(param1, value1);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} 
		catch (WBLocaleException e)
		{
			// OK
		}
		catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock, configurationMock);
			
	} catch (WBException e)
	{
		assertTrue (false);
	}
}


@Test
public void test_populateLocale()
{
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	Map<String, String> mapLocale = new HashMap<String, String>();
	mapLocale.put(WBModel.LOCALE_LANGUAGE_KEY, "en");
	mapLocale.put(WBModel.LOCALE_COUNTRY_KEY, "GB");
	WBModel model = new WBModel();
	
	try
	{
		Whitebox.invokeMethod(modelBuilder, "populateLocale", "en", "GB", model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(model.getCmsModel().get(WBModel.LOCALE_KEY).equals(mapLocale));

}

@Test
public void test_populateGlobalParameters()
{
	try
	{
		modelBuilder = new ModelBuilder(cacheInstancesMock);
		
		List<WBParameter> globalParams = new ArrayList<WBParameter>();
		WBModel model = new WBModel();
		Map<String, String> mapParams = new HashMap<String, String>(); 
		String param1 = "param1";
		String value1 = "value1";
		mapParams.put(param1, value1);
		
		WBParameter parameter = new WBParameter();
		parameter.setName(param1);
		parameter.setValue(value1);
		globalParams.add(parameter);
		
		
		WBParametersCache paramsCacheMock = EasyMock.createMock(WBParametersCache.class);		
		EasyMock.expect(cacheInstancesMock.getWBParameterCache()).andReturn(paramsCacheMock);
		EasyMock.expect(paramsCacheMock.getAllForOwner("")).andReturn(globalParams);
		
		EasyMock.replay(cacheInstancesMock, paramsCacheMock);
		
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateGlobalParameters", model);
		} catch (Exception e)
		{
			assertTrue(false);
		}
		EasyMock.verify(cacheInstancesMock, paramsCacheMock);
		
		assertTrue (model.getCmsModel().get(WBModel.GLOBALS_KEY).equals(mapParams));
		
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateStaticParametersFromHeader_http()
{
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	String url = "http://www.example.com/test/";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBModel.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBModel.GLOBAL_CONTEXT_PATH, "/test");
	mapStaticParams.put(WBModel.GLOBAL_BASE_URL, "http://www.example.com/test");
	WBModel model = new WBModel();
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(WBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParametersFromHeader_https()
{
	String url = "https://www.example.com/test1/test2";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBModel.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBModel.GLOBAL_CONTEXT_PATH, "/test1/test2");
	mapStaticParams.put(WBModel.GLOBAL_BASE_URL, "https://www.example.com/test1/test2");
	WBModel model = new WBModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParametersFromConfig_http()
{
	
	String url = "https://www.example.com/test1/test2";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(null);
	modelConfigs.put("baseModelUrlPath", "https://www.example.com/test1/test2/");
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBModel.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBModel.GLOBAL_CONTEXT_PATH, "/test1/test2");
	mapStaticParams.put(WBModel.GLOBAL_BASE_URL, "https://www.example.com/test1/test2");
	WBModel model = new WBModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParametersFromRequest_http()
{
	
	String url = "https://www.example.com/test1/test2/aaa.html?abc=1";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(null);
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(PublicContentServlet.CONTEXT_PATH)).andReturn("/test1/test2");
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBModel.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBModel.GLOBAL_CONTEXT_PATH, "/test1/test2");
	mapStaticParams.put(WBModel.GLOBAL_BASE_URL, "https://www.example.com/test1/test2");
	WBModel model = new WBModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_uppercase()
{
	String url = "http://www.EXAMPLE.com/test1/TEST2";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBModel.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBModel.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBModel.GLOBAL_CONTEXT_PATH, "/test1/TEST2");
	mapStaticParams.put(WBModel.GLOBAL_BASE_URL, "http://www.EXAMPLE.com/test1/TEST2");
	WBModel model = new WBModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WBModel.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_justdomain()
{
	String url = "http://EXAMPLE.com";
	EasyMock.expect(requestMock.getHeader(ModelBuilder.BASE_MODEL_URL_PATH_HEADER)).andReturn(url);
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBModel.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBModel.GLOBAL_DOMAIN, "example.com");
	mapStaticParams.put(WBModel.GLOBAL_CONTEXT_PATH, "");
	mapStaticParams.put(WBModel.GLOBAL_BASE_URL, "http://EXAMPLE.com");
	WBModel model = new WBModel();
	
	EasyMock.replay(requestMock, configurationMock);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	
	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock, configurationMock);
	assertTrue(model.getCmsModel().get(WBModel.REQUEST_KEY).equals(mapStaticParams));
}


}