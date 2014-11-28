/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cache.WPBProjectCache;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBLocaleException;
import com.webpagebytes.cms.utility.Pair;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

class ModelBuilder {

	private static final Logger log = Logger.getLogger(ModelBuilder.class.getName());
	private WPBCacheInstances cacheInstances;
	private CmsConfiguration configuration;
	private String baseModelUrlPath;
	public static final String BASE_MODEL_URL_PATH_HEADER = "X-BaseModelUrlPath";
	public ModelBuilder(WPBCacheInstances cacheInstances)
	{
		this.cacheInstances = cacheInstances;
		configuration = CmsConfigurationFactory.getConfiguration();
		Map<String, String> sectionParams = configuration.getSectionParams(WPBSECTION.SECTION_MODEL_CONFIGURATOR);
		if (sectionParams != null)
		{
			baseModelUrlPath = sectionParams.get("baseModelUrlPath");
		}
	}

	/*
	 * populate the WBModel based on uri data. This will populate:
	 * REQUEST_KEY
	 * GLOBALS_KEY
	 * URL_REQUEST_PARAMETERS_KEY
	 * 
	 */
	public void populateModelForUriData(HttpServletRequest request, WPBUri uri, URLMatcherResult urlMatcherResult, WPBModel model) throws WPBException
	{
		populateUriParameters(request, uri.getExternalKey(), urlMatcherResult, model);
		populateGlobalParameters(model);
		populateStaticParameters(request, model);
	}

	public void populateModelForWebPage(WPBWebPage page, WPBModel model) throws WPBException
	{
		WPBParametersCache parametersCache = cacheInstances.getWBParameterCache();
		
		List<WPBParameter> wbPageParams = parametersCache.getAllForOwner(page.getExternalKey());
		Map<String, String> pageParams = new HashMap<String, String>();
		for(WPBParameter param: wbPageParams)
		{
			pageParams.put(param.getName(), param.getValue());
		}
				
		model.getCmsModel().put(WPBModel.PAGE_PARAMETERS_KEY, pageParams);	
		
	}

	private void populateUriParameters(HttpServletRequest request, String uriExternalKey,
										URLMatcherResult urlMatcherResult, WPBModel model) throws WPBException
	{
		WPBProjectCache projectCache = cacheInstances.getProjectCache();
		WPBParametersCache parametersCache = cacheInstances.getWBParameterCache();
	
		// populate the URL_REQUEST_PARAMETERS_KEY
		Pair<String, String> defaultLocale = projectCache.getDefaultLocale();
		
		String languageParam = defaultLocale.getFirst();
		String countryParam = defaultLocale.getSecond();
		boolean hasLocaleParams = false;
		Set<String> supportedLanguages = projectCache.getSupportedLocales();
		
		Map<String, String> urlPatternParams = urlMatcherResult.getPatternParams();
		Map<String, String> uriParams = new HashMap<String, String>();
		
		List<WPBParameter> wbUriParams = parametersCache.getAllForOwner(uriExternalKey);
		for(WPBParameter param: wbUriParams)
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
			if (param.getLocaleType() == WPBParameter.PARAMETER_LOCALE_LANGUAGE)
			{
				hasLocaleParams = true;
				languageParam = urlPatternParams.get(paramKey);
			}
			if (param.getLocaleType() == WPBParameter.PARAMETER_LOCALE_COUNTRY)
			{
				hasLocaleParams = true;
				countryParam = urlPatternParams.get(paramKey);
			}			
		}
		
		if (languageParam == null || countryParam == null)
		{
			throw new WPBLocaleException("Locale params expected in request url but were not found");
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
				throw new WPBLocaleException("Project does not support locale  " + localeStr); 
			}
		}
			
		model.getCmsModel().put(WPBModel.URI_PARAMETERS_KEY, uriParams);
		
		populateLocale(languageParam, countryParam, model);
	}
	
	public void populateLocale(String language, String country, WPBModel model)
	{
		// populate the LOCALE_KEY
		Map<String, String> localeMap = new HashMap<String, String>();
		localeMap.put(WPBModel.LOCALE_LANGUAGE_KEY, language);
		localeMap.put(WPBModel.LOCALE_COUNTRY_KEY, country);
		model.getCmsModel().put(WPBModel.LOCALE_KEY, localeMap);				
	}
	
	public void populateGlobalParameters(WPBModel model) throws WPBException
	{
		// populate the GLOBALS_KEY
		Map<String, String> globalParams = new HashMap<String, String>();
		List<WPBParameter> wbGlobalParams = cacheInstances.getWBParameterCache().getAllForOwner("");
		for(WPBParameter param: wbGlobalParams)
		{
			globalParams.put(param.getName(), param.getValue());
		}
		model.getCmsModel().put(WPBModel.GLOBALS_KEY, globalParams);
		
	}
	private String getProtocol(String url)
	{
		int indexDomain = url.indexOf("://");
		if (indexDomain > 0)
		{
			return url.substring(0, indexDomain).toLowerCase();
		}
		return null;
	}
	private String getDomain(String url)
	{
		int indexDomain = url.indexOf("://");
		if (indexDomain > 0)
		{
			String domain = url.substring(indexDomain+3);
			int indexUri = domain.indexOf('/');
			if (indexUri>0)
			{
				domain = domain.substring(0, indexUri);
			}
			int indexPort = domain.indexOf(':');
			if (indexPort>0)
			{
				domain = domain.substring(0, indexPort);
			}
			return domain.toLowerCase();
		}
		return null;
	}
	private String getContextPathFromUrl(String url)
	{
		// for http://www.example.com/test return '/test'
		// for http://www.example.com return ''
		// for http://www.example.com/test1/test2 return '/test1/test2'
		int indexDomain = url.indexOf("://");
		if (indexDomain > 0)
		{
			String urlNoProtocol = url.substring(indexDomain+3);
			int index1 = urlNoProtocol.indexOf('/');
			int index2 = urlNoProtocol.lastIndexOf('/');
			if (index1<0 && index2 <0)
			{
				return "";
			}
			if (index1 == index2)
			{
				return urlNoProtocol.substring(index1);
			}
			
			// we are here in either /test/ or /test1/test2/ case
			if (index1>0 && index2>0 && (index2 == urlNoProtocol.length()-1))
			{
				return urlNoProtocol.substring(index1, index2-1);
			}
			
			return urlNoProtocol.substring(index1);

		}
		return "";		
	}
	
	private void populateStaticParameters(HttpServletRequest request, WPBModel model)
	{
		//the static path params are taken in the following order
		// the header value (X-BaseModelUrlPath), the value of baseModelUrlPath from configuration and finally the requestUrl
		String url = request.getHeader(BASE_MODEL_URL_PATH_HEADER);
		boolean useUrlfromRequest = false;
		if (null == url)
		{
			url = baseModelUrlPath; 
			if (null == url)
			{
				useUrlfromRequest = true;
				url = request.getRequestURL().toString().toLowerCase();
				int indexQ = url.indexOf('?');
				if (indexQ > 0)
				{
					url = url.substring(0, indexQ-1);
				}
			}
		}
		log.log(Level.INFO, "Build WBModel.REQUEST_KEY for url:" + url);
	
		String protocol = getProtocol(url);
		String domain = getDomain(url);

		Map<String, String> result = new HashMap<String, String>();
		result.put(WPBModel.GLOBAL_PROTOCOL, protocol);
		result.put(WPBModel.GLOBAL_DOMAIN, domain);
		
		if (useUrlfromRequest)
		{
			String baseUrl = protocol + "://" + domain;
			Object objUriPrefix = request.getAttribute(WPBPublicContentServlet.CONTEXT_PATH);
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
				result.put(WPBModel.GLOBAL_CONTEXT_PATH, objUriPrefix.toString());
			}	
			result.put(WPBModel.GLOBAL_BASE_URL, baseUrl);
		} else
		{
			if (url.lastIndexOf('/') == url.length()-1)
			{
				url = url.substring(0,  url.length()-1);
			}
			result.put(WPBModel.GLOBAL_BASE_URL, url);
			result.put(WPBModel.GLOBAL_CONTEXT_PATH, getContextPathFromUrl(url));
		}
		
		model.getCmsModel().put(WPBModel.REQUEST_KEY, result);
	}

}
