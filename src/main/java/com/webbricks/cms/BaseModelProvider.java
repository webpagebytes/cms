package com.webbricks.cms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.webbricks.appinterfaces.WBCmsModel;
import com.webbricks.appinterfaces.WBModel;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBParametersCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBPredefinedParameters;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.exception.WBLocaleException;

public class BaseModelProvider {
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
	
	public static final String UUID_PROTOCOL = "403a0a8d-7959-472d-b793-c66e5ec71a2e";
	public static final String UUID_DOMAIN = "d27b1bb8-f5c7-4d1a-a36e-0ccfb2525d3c";
	public static final String UUID_URI_PREFIX = "b239ad48-1aee-46dc-87b6-048ff2254d59";

	protected WBCacheInstances cacheInstances;
	
	public BaseModelProvider(WBCacheInstances cacheInstances)
	{
		this.cacheInstances = cacheInstances;
	}

	private Set<String> getSupportedLanguages(WBProject project)
	{
		String supportedLanguages = project.getSupportedLanguages();
		String[] langs = supportedLanguages.split(",");
		Set<String> supportedLanguagesSet = new HashSet<String>();
		for(String lang: langs)
		{
			if (lang.length()>0) supportedLanguagesSet.add(lang);
		}
		return supportedLanguagesSet;
	}
	
	private Map<String, String> getStaticParameters(HttpServletRequest request)
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
		return result;
	}
	
	private Map<String, String> getGlobalParameters(WBParametersCache parametersCache) throws WBIOException
	{
		List<WBParameter> globalParams = parametersCache.getAllForOwner("");
		Map<String, String> result = new HashMap<String, String>();
		
		for(WBParameter param: globalParams)
		{
			result.put(param.getName(), param.getValue());
		}
		return result;
		
	}
	public WBCmsModel getControllerModel(HttpServletRequest request,
			URLMatcherResult urlMatcherResult, 
			String ownerExternalKey,
			String ownerModelKey,
			WBProject project) throws WBException
	{
		WBCmsModel model = new WBCmsModel();
		Map<String, String> subUrlParams = new HashMap<String, String>();
		
		if (urlMatcherResult.getPatternParams() != null)
		{
			subUrlParams.putAll(urlMatcherResult.getPatternParams());
		}
		model.put(URL_REQUEST_PARAMETERS_KEY, subUrlParams);

		List<WBParameter> ownerParams = cacheInstances.getWBParameterCache().getAllForOwner(ownerExternalKey);
		
		Map<String, String> ownerParamsMap = new HashMap<String, String>();
		
		Map<String, String[]> requestQueryParams = request.getParameterMap();
	
		boolean languageParamPresent = false;
		
		String localeLanguage = "";
		String localeCountry = "";
		if (ownerParams != null)
		{
			for (WBParameter wbParam: ownerParams)
			{
				ownerParamsMap.put(wbParam.getName(), wbParam.getValue());
				
				if (wbParam.getOverwriteFromUrl() != null && wbParam.getOverwriteFromUrl() != 0)
				{
					if ((wbParam.getLocaleType() != null) && (wbParam.getLocaleType() != 0))
					{
						if (wbParam.getLocaleType() == 1)
						{
							String _locale = subUrlParams.get(wbParam.getName());
							if (_locale != null)
							{
								languageParamPresent = true;
								localeLanguage = _locale;
							}
						} else
						if (wbParam.getLocaleType() == 2)
						{
							String _country = subUrlParams.get(wbParam.getName());
							if (_country != null)
							{
								localeCountry = _country;
							}
						}
					} else
					if (requestQueryParams.containsKey(wbParam.getName()))
					{
						ownerParamsMap.put(wbParam.getName(), requestQueryParams.get(wbParam.getName())[0]);
					}
				}
			}
		}		
		
		model.put(ownerModelKey, ownerParamsMap);
		model.put(GLOBALS_KEY, getGlobalParameters(cacheInstances.getWBParameterCache()));
		
		model.put(REQUEST_KEY, getStaticParameters(request));
		
		Map<String, String> localeModel = new HashMap<String, String>();
		
		if (languageParamPresent == true)
		{
			Set<String> supportedLanguagesSet = getSupportedLanguages(project);
			String lcid = localeLanguage;
			if (localeCountry.length()>0)
			{
				lcid = lcid.concat("_").concat(localeCountry);
			}
			if (!supportedLanguagesSet.contains(lcid))
			{
				throw new WBLocaleException("Locale not supported in %s %s ".format(supportedLanguagesSet.toString(),lcid));
			}
			localeModel.put(PageContentBuilder.LOCALE_LANGUAGE_KEY, localeLanguage);
			localeModel.put(PageContentBuilder.LOCALE_COUNTRY_KEY, localeCountry);
		} else
		{
			String defaultLanguage = cacheInstances.getProjectCache().getDefaultLanguage();
			String[] langs_ = defaultLanguage.split("_");
			localeModel.put(PageContentBuilder.LOCALE_LANGUAGE_KEY, langs_[0]);
			localeModel.put(PageContentBuilder.LOCALE_COUNTRY_KEY, (langs_.length == 1) ? "":langs_[1] );					
		}
		
		model.put(LOCALE_KEY , localeModel);

		return model;
	}

}
