package com.webbricks.cms;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbricks.appinterfaces.IPageModelProvider;
import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBParametersCache;
import com.webbricks.cache.WBProjectCache;
import com.webbricks.cache.WBUrisCache;
import com.webbricks.cache.WBWebPagesCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBPredefinedParameters;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.controllers.WBController;
import com.webbricks.exception.WBContentException;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.exception.WBLocaleCountryException;
import com.webbricks.exception.WBLocaleException;
import com.webbricks.exception.WBLocaleLanguageException;
import com.webbricks.template.WBFreeMarkerTemplateEngine;
import com.webbricks.template.WBTemplateEngine;

/*
 * A Page content is created from a page model + a page template.
 * A page will have a parameters model instance - pageParamsModel - a key-value map. This is build in the following order 
 * 1) the url will produce a set of sub url parameters (i.e /news/{keywords}-{key}) provided as a map of key values, added to pageParamsModel
 * 2) the page will have a set of WBParameter objects that will be added to pageParamsModel
 * 3) the url parameters (the ones after ? i.e /news/sports-football-123?showComments=true) will be added to pageParamsModel if there is a 
 * corresponding WBParameter with overwriteFromUrl flag
 * 4) the page controller will receive the pageParamsModel and will be able to add new model data
 * 
 * 
 */
public class PageContentBuilder {
	public static final String URL_PARAMETERS_KEY = "wbUrlParams";
	public static final String PAGE_PARAMETERS_KEY = "wbPageParams";
	public static final String PAGE_CONTROLLER_MODEL_KEY = "wbControllerModel";
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
	
	
	
	private static final Logger log = Logger.getLogger(PageContentBuilder.class.getName());
	
	private WBTemplateEngine templateEngine;
	private WBCacheInstances cacheInstances;
	private Map<String, Object> customControllers;
	
	public PageContentBuilder()
	{
	}

	public PageContentBuilder(WBCacheInstances cacheInstances)
							
	{
		this.customControllers = new HashMap<String, Object>();
		this.cacheInstances = cacheInstances;
	}
	
	public void initialize() throws WBException
	{
		try
		{
			templateEngine = new WBFreeMarkerTemplateEngine(cacheInstances);
			templateEngine.initialize();
		} catch (WBException e)
		{
			throw e;
		}
	}
	
	public WBWebPage findWebPage(String pageExternalKey) throws WBException
	{
		WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().getByExternalKey(pageExternalKey);
		return wbWebPage;
		
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
	
	private void addStaticParameters(HttpServletRequest request, Map<String, String> pageModel)
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
		pageModel.put(WBPredefinedParameters.GLOBAL_PROTOCOL, protocol);
		pageModel.put(WBPredefinedParameters.GLOBAL_DOMAIN, domain);
		Object objUriPrefix = request.getAttribute(PublicContentServlet.URI_PREFIX);
		if (objUriPrefix != null)
		{
			pageModel.put(WBPredefinedParameters.GLOBAL_URI_PREFIX, (String)objUriPrefix);
		}	
	}
	
	private Map<String, Object> getPageModel(HttpServletRequest request,
			URLMatcherResult urlMatcherResult, 
			WBWebPage wbWebPage, 
			WBProject project) throws WBException
	{
		Map<String, Object> pageModel = new HashMap<String, Object>();
		Map<String, String> subUrlParams = new HashMap<String, String>();
		
		if (urlMatcherResult.getPatternParams() != null)
		{
			subUrlParams.putAll(urlMatcherResult.getPatternParams());
		}
		pageModel.put(URL_PARAMETERS_KEY, subUrlParams);

		List<WBParameter> pageWbParams = cacheInstances.getWBParameterCache().getAllForOwner(wbWebPage.getExternalKey());
		List<WBParameter> globalParams = cacheInstances.getWBParameterCache().getAllForOwner("");
		globalParams.addAll(pageWbParams);
		pageWbParams = globalParams;
		
		Map<String, String> pageParams = new HashMap<String, String>();
		
		Map<String, String[]> requestQueryParams = request.getParameterMap();
	
		Set<String> supportedLanguagesSet = getSupportedLanguages(project);
		
		boolean languageParamPresent = false;
		
		String localeLanguage = "";
		String localeCountry = "";
		if (pageWbParams != null)
		{
			for (WBParameter wbParam: pageWbParams)
			{
				pageParams.put(wbParam.getName(), wbParam.getValue());
				
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
						pageParams.put(wbParam.getName(), requestQueryParams.get(wbParam.getName())[0]);
					}
				}
			}
		}
		
		addStaticParameters(request, pageParams);
		
		if (languageParamPresent == true)
		{
			String lcid = localeLanguage;
			if (localeCountry.length()>0)
			{
				lcid = lcid.concat("_").concat(localeCountry);
			}
			if (!supportedLanguagesSet.contains(lcid))
			{
				String supportedLangs = "";
				throw new WBLocaleException("Locale not supported in %s %s ".format(supportedLanguagesSet.toString(),lcid));
			}
			pageModel.put(PageContentBuilder.LOCALE_LANGUAGE_KEY, localeLanguage);
			pageModel.put(PageContentBuilder.LOCALE_COUNTRY_KEY, localeCountry);
		} else
		{
			String defaultLanguage = cacheInstances.getProjectCache().getDefaultLanguage();
			String[] langs_ = defaultLanguage.split("_");
			pageModel.put(PageContentBuilder.LOCALE_LANGUAGE_KEY, langs_[0]);
			pageModel.put(PageContentBuilder.LOCALE_COUNTRY_KEY, (langs_.length == 1) ? "":langs_[1] );					
		}
		
		pageModel.put(PAGE_PARAMETERS_KEY ,pageParams);

		return pageModel;
	}
	
	public String buildPageContent(HttpServletRequest request,
			URLMatcherResult urlMatcherResult, 
			WBWebPage wbWebPage, 
			WBProject project) throws WBException
	{

		if ((wbWebPage.getIsTemplateSource() == null) || (wbWebPage.getIsTemplateSource() == 0))
		{
			return wbWebPage.getHtmlSource();
		}
		
		Map<String, Object> pageModel = getPageModel(request, urlMatcherResult, wbWebPage, project);

		String controllerClassName = wbWebPage.getPageModelProvider();
		if (controllerClassName !=null && controllerClassName.length()>0)
		{
			Map<String, Object> controllerModel = new HashMap<String, Object>();
			IPageModelProvider controllerInst = null;
			if (customControllers.containsKey(controllerClassName))
			{
				controllerInst = (IPageModelProvider) customControllers.get(controllerClassName);
			} else
			{
				try {
				controllerInst = (IPageModelProvider) Class.forName(controllerClassName).newInstance();
				} catch (Exception e) { throw new WBException("Cannot instantiate page controller " + controllerClassName, e); }			
			}
			if (controllerInst != null)
			{
				controllerInst.getPageModel(request, controllerModel);
				pageModel.put(PAGE_CONTROLLER_MODEL_KEY, controllerModel);
			}
			
		}
		String result = "";
		try {
			StringWriter out = new StringWriter();
			
			templateEngine.process(WBTemplateEngine.WEBPAGES_PATH_PREFIX + wbWebPage.getName(), pageModel, out);
			result += out.toString();
		} catch (WBException e)
		{
			throw e;
		}
		
		return result;
	}

}
