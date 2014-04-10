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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.appinterfaces.WBModel;
import com.webpagebytes.cache.WBCacheInstances;
import com.webpagebytes.cache.WBParametersCache;
import com.webpagebytes.cache.WBProjectCache;
import com.webpagebytes.cms.ModelBuilder;
import com.webpagebytes.cms.Pair;
import com.webpagebytes.cms.PublicContentServlet;
import com.webpagebytes.cms.URLMatcherResult;
import com.webpagebytes.cmsdata.WBParameter;
import com.webpagebytes.cmsdata.WBPredefinedParameters;
import com.webpagebytes.cmsdata.WBUri;
import com.webpagebytes.cmsdata.WBWebPage;
import com.webpagebytes.controllers.WBLanguagesController;
import com.webpagebytes.exception.WBException;
import com.webpagebytes.exception.WBLocaleException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ModelBuilder.class})
public class TestModelBuilder {
	
HttpServletRequest requestMock;
WBCacheInstances cacheInstancesMock;
ModelBuilder modelBuilder;
WBUri uriMock;
URLMatcherResult urlMatcherResultMock;
WBModel modelMock;
WBProjectCache projectCacheMock;
WBWebPage webPageMock;

@Before
public void setUp()
{
	requestMock = EasyMock.createMock(HttpServletRequest.class);
	cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
	modelBuilder = new ModelBuilder(cacheInstancesMock);
	uriMock = EasyMock.createMock(WBUri.class);
	urlMatcherResultMock = EasyMock.createMock(URLMatcherResult.class);
	modelMock = EasyMock.createMock(WBModel.class);
	webPageMock = EasyMock.createMock(WBWebPage.class);
	projectCacheMock  = EasyMock.createMock(WBProjectCache.class);
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
		EasyMock.replay(requestMock, uriMock, urlMatcherResultMock, modelMock);
		modelBuilder.populateModelForUriData(requestMock, uriMock, urlMatcherResultMock, modelMock);		
		EasyMock.verify(requestMock, uriMock, urlMatcherResultMock, modelMock);
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
		
		EasyMock.replay(requestMock, webPageMock, cacheInstancesMock, paramsCacheMock);
		
		modelBuilder.populateModelForWebPage(requestMock, webPageMock, model);		
		EasyMock.verify(requestMock, webPageMock, cacheInstancesMock, paramsCacheMock);
		
		assertTrue (model.getCmsModel().get(ModelBuilder.PAGE_PARAMETERS_KEY).equals(mapParams));
		
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
		EasyMock.expect(projectCacheMock.getSupportedLanguages()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
		
		assertTrue (model.getCmsModel().get(ModelBuilder.URL_REQUEST_PARAMETERS_KEY).equals(mapParams));
		
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
		EasyMock.expect(projectCacheMock.getSupportedLanguages()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
		
		assertTrue (model.getCmsModel().get(ModelBuilder.URL_REQUEST_PARAMETERS_KEY).equals(mapParams));
		
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
		EasyMock.expect(projectCacheMock.getSupportedLanguages()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
		try
		{
			Whitebox.invokeMethod(modelBuilder, "populateUriParameters", requestMock, uriExternalKey, urlMatcherResult, model);
		} catch(Exception e)
		{
			assertTrue(false);
		}
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
		
		assertTrue (model.getCmsModel().get(ModelBuilder.URL_REQUEST_PARAMETERS_KEY).equals(mapParams));
		
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
		EasyMock.expect(projectCacheMock.getSupportedLanguages()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		patternParams.put(param1, value1);
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
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
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
				
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
		EasyMock.expect(projectCacheMock.getSupportedLanguages()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		// pattern params contains only the xyz param
		patternParams.put(param2, value2);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
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
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
			
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
		EasyMock.expect(projectCacheMock.getSupportedLanguages()).andReturn(supportedLanguages);
		
		URLMatcherResult urlMatcherResult = new URLMatcherResult();
		Map<String, String> patternParams = new HashMap<String, String>();
		// pattern params contains only the language param
		patternParams.put(param1, value1);
		urlMatcherResult.setPatternParams(patternParams);
		
		EasyMock.expect(paramsCacheMock.getAllForOwner(uriExternalKey)).andReturn(pageParams);
		
		
		EasyMock.replay(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
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
		
		EasyMock.verify(requestMock, cacheInstancesMock, paramsCacheMock, projectCacheMock);
			
	} catch (WBException e)
	{
		assertTrue (false);
	}
}


@Test
public void test_populateLocale()
{
	Map<String, String> mapLocale = new HashMap<String, String>();
	mapLocale.put(ModelBuilder.LOCALE_LANGUAGE_KEY, "en");
	mapLocale.put(ModelBuilder.LOCALE_COUNTRY_KEY, "GB");
	WBModel model = new WBModel();
	
	try
	{
		Whitebox.invokeMethod(modelBuilder, "populateLocale", "en", "GB", model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(model.getCmsModel().get(ModelBuilder.LOCALE_KEY).equals(mapLocale));

}

@Test
public void test_populateGlobalParameters()
{
	try
	{
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
		
		assertTrue (model.getCmsModel().get(ModelBuilder.GLOBALS_KEY).equals(mapParams));
		
	} catch (WBException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_populateStaticParameters_http()
{
	String url = "http://www.example.com/test/xyz.html?a=1&b=2";
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_URI_PREFIX, "/test");
	WBModel model = new WBModel();
	
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(PublicContentServlet.URI_PREFIX)).andReturn("/test");
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(ModelBuilder.REQUEST_KEY).equals(mapStaticParams));

}

@Test
public void test_populateStaticParameters_https()
{
	String url = "https://www.example.com/test/xyz.html?a=1&b=2";
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_PROTOCOL, "https");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_DOMAIN, "www.example.com");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_URI_PREFIX, "/test");
	WBModel model = new WBModel();
	
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(PublicContentServlet.URI_PREFIX)).andReturn("/test");
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(ModelBuilder.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_uppercase()
{
	String url = "HTTP://EXAMPLE.COM/TEST/xyz.html";
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_DOMAIN, "example.com");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_URI_PREFIX, "/test");
	WBModel model = new WBModel();
	
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(PublicContentServlet.URI_PREFIX)).andReturn("/test");
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(ModelBuilder.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_justdomain()
{
	String url = "HTTP://EXAMPLE.COM";
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_DOMAIN, "example.com");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_URI_PREFIX, "/");
	
	WBModel model = new WBModel();
	
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(PublicContentServlet.URI_PREFIX)).andReturn("/");
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(ModelBuilder.REQUEST_KEY).equals(mapStaticParams));
}

@Test
public void test_populateStaticParameters_nouriprefix()
{
	String url = "HTTP://EXAMPLE.COM";
	Map<String, String> mapStaticParams = new HashMap<String, String>();
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_PROTOCOL, "http");
	mapStaticParams.put(WBPredefinedParameters.GLOBAL_DOMAIN, "example.com");
	
	WBModel model = new WBModel();
	
	EasyMock.expect(requestMock.getRequestURL()).andReturn(new StringBuffer(url));
	EasyMock.expect(requestMock.getAttribute(PublicContentServlet.URI_PREFIX)).andReturn(null);
	
	EasyMock.replay(requestMock);

	try
	{		
		Whitebox.invokeMethod(modelBuilder, "populateStaticParameters", requestMock, model);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	EasyMock.verify(requestMock);
	assertTrue(model.getCmsModel().get(ModelBuilder.REQUEST_KEY).equals(mapStaticParams));
}


}