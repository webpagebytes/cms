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

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBParametersCache;
import com.webbricks.cache.WBProjectCache;
import com.webbricks.cache.WBUrisCache;
import com.webbricks.cache.WBWebPagesCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
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
	public static final String LOCALE_LANGUAGE_KEY = "wbLocaleLanguage";
	public static final String LOCALE_COUNTRY_KEY = "wbLocaleCountry";
	public static final String LOCALE_MESSAGES = "wbMessages";
	public static final String MODULE_DIRECTIVE = "wbModule";
	public static final String IMAGE_DIRECTIVE = "wbImage";
	public static final String ARTICLE_DIRECTIVE = "wbArticle";
	public static final String TEXT_FORMAT_DIRECTIVE = "wbFormatText";
	public static final String FORMAT_TEXT_METHOD = "wbFormatText";
	
	
	
	
	private static final Logger log = Logger.getLogger(PageContentBuilder.class.getName());
	
	private WBTemplateEngine templateEngine;
	private WBCacheInstances cacheInstances;
	
	public PageContentBuilder()
	{
	}

	public PageContentBuilder(WBCacheInstances cacheInstances)
							
	{
		this.cacheInstances = cacheInstances;
	}
	
	public void initialize() throws WBIOException
	{
		try
		{
			templateEngine = new WBFreeMarkerTemplateEngine(cacheInstances);
			templateEngine.initialize();
		} catch (IOException e)
		{
			throw new WBIOException("Cannot initialize WB Template Engine", e);
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
		
		boolean languageParamsPresent = false;
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
							localeLanguage = subUrlParams.get(wbParam.getName());
							languageParamsPresent = true;
							if (localeLanguage == null)
							{
								throw new WBLocaleLanguageException("No locale language parameter for " + wbParam.getName());
							}
						} else
						if (wbParam.getLocaleType() == 2)
						{
							localeCountry = subUrlParams.get(wbParam.getName());
							languageParamsPresent = true;
							if (localeCountry == null)
							{
								throw new WBLocaleCountryException("No locale country parameter for " + wbParam.getName());
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
		if (languageParamsPresent == true)
		{
			String lcid = localeLanguage;
			if (localeCountry.length()>0)
			{
				lcid = lcid.concat("_").concat(localeCountry);
			}
			if (!supportedLanguagesSet.contains(lcid))
			{
				throw new WBLocaleException("Locale not supported " + lcid);
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

		if (wbWebPage.getIsTemplateSource() == 0)
		{
			return wbWebPage.getHtmlSource();
		}
		
		Map<String, Object> pageModel = getPageModel(request, urlMatcherResult, wbWebPage, project);

		String result = "";
		try {
			StringWriter out = new StringWriter();
			
			templateEngine.process(WBTemplateEngine.WEBPAGES_PATH_PREFIX + wbWebPage.getName(), pageModel, out);
			result += out.toString();
		} catch (IOException e)
		{
			log.log(Level.SEVERE, "ERROR: ", e);
			
			throw new WBContentException("Can't generate content for request " + urlMatcherResult.getUrlRequest());
		}
		
		return result;
	}

}
