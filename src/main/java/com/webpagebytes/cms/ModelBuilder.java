package com.webpagebytes.cms;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBParametersCache;
import com.webpagebytes.cms.cache.WBProjectCache;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBLocaleException;

public class ModelBuilder {

	private WBCacheInstances cacheInstances;
	
	public ModelBuilder(WBCacheInstances cacheInstances)
	{
		this.cacheInstances = cacheInstances;
	}
	
	/*
	 * populate the WBModel based on uri data. This will populate:
	 * REQUEST_KEY
	 * GLOBALS_KEY
	 * URL_REQUEST_PARAMETERS_KEY
	 * 
	 */
	public void populateModelForUriData(HttpServletRequest request, WBUri uri, URLMatcherResult urlMatcherResult, WBModel model) throws WBException
	{
		populateUriParameters(request, uri.getExternalKey(), urlMatcherResult, model);
		populateGlobalParameters(model);
		populateStaticParameters(request, model);
	}

	public void populateModelForWebPage(WBWebPage page, WBModel model) throws WBException
	{
		WBParametersCache parametersCache = cacheInstances.getWBParameterCache();
		
		List<WBParameter> wbPageParams = parametersCache.getAllForOwner(page.getExternalKey());
		Map<String, String> pageParams = new HashMap<String, String>();
		for(WBParameter param: wbPageParams)
		{
			pageParams.put(param.getName(), param.getValue());
		}
				
		model.getCmsModel().put(WBModel.PAGE_PARAMETERS_KEY, pageParams);	
		
	}

	private void populateUriParameters(HttpServletRequest request, String uriExternalKey,
										URLMatcherResult urlMatcherResult, WBModel model) throws WBException
	{
		WBProjectCache projectCache = cacheInstances.getProjectCache();
		WBParametersCache parametersCache = cacheInstances.getWBParameterCache();
	
		// populate the URL_REQUEST_PARAMETERS_KEY
		Pair<String, String> defaultLocale = projectCache.getDefaultLocale();
		
		String languageParam = defaultLocale.getFirst();
		String countryParam = defaultLocale.getSecond();
		boolean hasLocaleParams = false;
		Set<String> supportedLanguages = projectCache.getSupportedLocales();
		
		Map<String, String> urlPatternParams = urlMatcherResult.getPatternParams();
		Map<String, String> uriParams = new HashMap<String, String>();
		
		List<WBParameter> wbUriParams = parametersCache.getAllForOwner(uriExternalKey);
		for(WBParameter param: wbUriParams)
		{
			String paramKey = param.getName();
			if (urlPatternParams == null)
			{
				uriParams.put(paramKey, param.getValue());
				continue;
			}
			if (urlPatternParams.containsKey(paramKey) && param.getOverwriteFromUrl() == 1)
			{
				uriParams.put(paramKey, urlPatternParams.get(paramKey));
			} else
			{
				uriParams.put(paramKey, param.getValue());
			}
			if (param.getLocaleType() == WBParameter.PARAMETER_LOCALE_LANGUAGE)
			{
				hasLocaleParams = true;
				languageParam = urlPatternParams.get(paramKey);
			}
			if (param.getLocaleType() == WBParameter.PARAMETER_LOCALE_COUNTRY)
			{
				hasLocaleParams = true;
				countryParam = urlPatternParams.get(paramKey);
			}			
		}
		
		if (languageParam == null || countryParam == null)
		{
			throw new WBLocaleException("Locale params expected in request url but were not found");
		} 
		if (hasLocaleParams)
		{
			String localeStr = languageParam;
			if (countryParam.length()>0)
			{
				localeStr = localeStr.concat("_").concat(countryParam);
			}
			if (!supportedLanguages.contains(localeStr))
			{
				throw new WBLocaleException("Project does not support locale  " + localeStr); 
			}
		}
			
		model.getCmsModel().put(WBModel.URI_PARAMETERS_KEY, uriParams);
		
		populateLocale(languageParam, countryParam, model);
	}
	
	public void populateLocale(String language, String country, WBModel model)
	{
		// populate the LOCALE_KEY
		Map<String, String> localeMap = new HashMap<String, String>();
		localeMap.put(WBModel.LOCALE_LANGUAGE_KEY, language);
		localeMap.put(WBModel.LOCALE_COUNTRY_KEY, country);
		model.getCmsModel().put(WBModel.LOCALE_KEY, localeMap);				
	}
	
	public void populateGlobalParameters(WBModel model) throws WBException
	{
		// populate the GLOBALS_KEY
		Map<String, String> globalParams = new HashMap<String, String>();
		List<WBParameter> wbGlobalParams = cacheInstances.getWBParameterCache().getAllForOwner("");
		for(WBParameter param: wbGlobalParams)
		{
			globalParams.put(param.getName(), param.getValue());
		}
		model.getCmsModel().put(WBModel.GLOBALS_KEY, globalParams);
		
	}
	private void populateStaticParameters(HttpServletRequest request, WBModel model)
	{
		String url = request.getRequestURL().toString().toLowerCase();
		int indexDomain = url.indexOf("://");
		String protocol = url.substring(0, indexDomain);
		String domain = url.substring(indexDomain+3);
		int indexUri = domain.indexOf('/');
		if (indexUri>0)
		{
			domain = domain.substring(0, indexUri);
		}
		Map<String, String> result = new HashMap<String, String>();
		result.put(WBModel.GLOBAL_PROTOCOL, protocol);
		result.put(WBModel.GLOBAL_DOMAIN, domain);
		String baseUrl = protocol + "://" + domain;
		Object objUriPrefix = request.getAttribute(PublicContentServlet.CONTEXT_PATH);
		if (objUriPrefix != null)
		{
			String uriPrefix = objUriPrefix.toString();
			if (uriPrefix.length()>0)
			{
				if (uriPrefix.startsWith("/"))
				{
					baseUrl = baseUrl + uriPrefix;
				} else
				{
					baseUrl = baseUrl + "/" + uriPrefix;
				}
			}
			result.put(WBModel.GLOBAL_CONTEXT_PATH, objUriPrefix.toString());
		}	
		result.put(WBModel.GLOBAL_BASE_URL, baseUrl);
		model.getCmsModel().put(WBModel.REQUEST_KEY, result);
	}

}
