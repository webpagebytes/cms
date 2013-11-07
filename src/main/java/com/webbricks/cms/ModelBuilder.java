package com.webbricks.cms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.webbricks.appinterfaces.WBModel;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBParametersCache;
import com.webbricks.cache.WBProjectCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBPredefinedParameters;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.exception.WBLocaleException;
import com.webbricks.exception.WBLocaleLanguageException;

public class ModelBuilder {
	
	public static final String GLOBALS_KEY = "wbGlobals";
	public static final String REQUEST_KEY = "wbRequest";
	public static final String URL_REQUEST_PARAMETERS_KEY = "wbRequestUrlParams";
	public static final String PAGE_PARAMETERS_KEY = "wbPageParams";
	public static final String URI_PARAMETERS_KEY = "wbUriParams";
	
	public static final String PAGE_CONTROLLER_MODEL_KEY = "wbPageModel";
	public static final String URI_CONTROLLER_MODEL_KEY = "wbUriModel";
	
	public static final String LOCALE_KEY = "wbLocale";
	public static final String LOCALE_LANGUAGE_KEY = "wbLocaleLanguage";
	public static final String LOCALE_COUNTRY_KEY = "wbLocaleCountry";
	public static final String LOCALE_MESSAGES = "wbMessages";
	public static final String MODULE_DIRECTIVE = "wbModule";
	public static final String IMAGE_DIRECTIVE = "wbImage";
	public static final String ARTICLE_DIRECTIVE = "wbArticle";
	public static final String TEXT_FORMAT_DIRECTIVE = "wbFormatText";
	public static final String FORMAT_TEXT_METHOD = "wbFormatText";
	
	
	public static final String GLOBAL_PROTOCOL = "WB_GLOBAL_PROTOCOL";
	public static final String GLOBAL_DOMAIN = "WB_GLOBAL_DOMAIN";
	public static final String GLOBAL_URI_PREFIX = "WB_GLOBAL_URI_PREFIX";


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

	public void populateModelForWebPage(HttpServletRequest request, WBWebPage page, WBModel model) throws WBException
	{
		WBParametersCache parametersCache = cacheInstances.getWBParameterCache();
		
		List<WBParameter> wbPageParams = parametersCache.getAllForOwner(page.getExternalKey());
		Map<String, String> pageParams = new HashMap<String, String>();
		for(WBParameter param: wbPageParams)
		{
			pageParams.put(param.getName(), param.getValue());
		}
				
		model.getCmsModel().put(PAGE_PARAMETERS_KEY, pageParams);	
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
		Set<String> supportedLanguages = projectCache.getSupportedLanguages();
		
		Map<String, String> urlPatternParams = urlMatcherResult.getPatternParams();

		Map<String, String> requestParams = new HashMap<String, String>();
		List<WBParameter> wbUriParams = parametersCache.getAllForOwner(uriExternalKey);
		for(WBParameter param: wbUriParams)
		{
			String paramKey = param.getName();
			if (urlPatternParams != null && urlPatternParams.containsKey(paramKey) && param.getOverwriteFromUrl() != null && param.getOverwriteFromUrl() == 1)
			{
				requestParams.put(paramKey, urlPatternParams.get(paramKey));
			} else
			{
				requestParams.put(paramKey, param.getValue());
			}
			if (param.getLocaleType() == WBParameter.PARAMETER_LOCALE_LANGUAGE)
			{
				hasLocaleParams = true;
				if (urlPatternParams != null)
					languageParam = urlPatternParams.get(paramKey);
				else 
					languageParam = null;
			}
			if (param.getLocaleType() == WBParameter.PARAMETER_LOCALE_COUNTRY)
			{
				hasLocaleParams = true;
				if (urlPatternParams != null)
					countryParam = urlPatternParams.get(paramKey);
				else
					countryParam = null;
			}			
		}
		
		if (languageParam == null || countryParam == null)
		{
			throw new WBLocaleException("Locale params expected in request url but were not found");
		} else
		if (hasLocaleParams && languageParam != null && languageParam.length()>0 && countryParam != null)
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
			
		model.getCmsModel().put(URL_REQUEST_PARAMETERS_KEY, requestParams);
		
		populateLocale(languageParam, countryParam, model);
	}
	
	private void populateLocale(String language, String country, WBModel model) throws WBException
	{
		// populate the LOCALE_KEY
		Map<String, String> localeMap = new HashMap<String, String>();
		localeMap.put(LOCALE_LANGUAGE_KEY, language);
		localeMap.put(LOCALE_COUNTRY_KEY, country);
		model.getCmsModel().put(LOCALE_KEY, localeMap);				
	}
	
	private void populateGlobalParameters (WBModel model) throws WBException
	{
		// populate the GLOBALS_KEY
		Map<String, String> globalParams = new HashMap<String, String>();
		List<WBParameter> wbGlobalParams = cacheInstances.getWBParameterCache().getAllForOwner("");
		for(WBParameter param: wbGlobalParams)
		{
			globalParams.put(param.getName(), param.getValue());
		}
		model.getCmsModel().put(GLOBALS_KEY, globalParams);
		
	}
	private void populateStaticParameters(HttpServletRequest request, WBModel model)
	{
		String url = request.getRequestURL().toString();
		int indexDomain = url.indexOf("://");
		String protocol = url.substring(0, indexDomain);
		String domain = url.substring(indexDomain+3);
		int indexUri = domain.indexOf('/');
		if (indexUri>0)
		{
			domain = domain.substring(0, indexUri);
		}
		Map<String, String> result = new HashMap<String, String>();
		result.put(WBPredefinedParameters.GLOBAL_PROTOCOL, protocol);
		result.put(WBPredefinedParameters.GLOBAL_DOMAIN, domain);
		Object objUriPrefix = request.getAttribute(PublicContentServlet.URI_PREFIX);
		if (objUriPrefix != null)
		{
			result.put(WBPredefinedParameters.GLOBAL_URI_PREFIX, (String)objUriPrefix);
		}	
		model.getCmsModel().put(BaseModelProvider.REQUEST_KEY, result);
	}

}
