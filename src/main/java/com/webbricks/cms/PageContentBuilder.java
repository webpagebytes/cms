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
public class PageContentBuilder extends BaseModelProvider {
	
	
	
	private static final Logger log = Logger.getLogger(PageContentBuilder.class.getName());
	
	private WBTemplateEngine templateEngine;
	private WBCacheInstances cacheInstances;
	private Map<String, Object> customControllers;
	

	public PageContentBuilder(WBCacheInstances cacheInstances)
							
	{
		super(cacheInstances);
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
		
	
	public String buildPageContent(HttpServletRequest request,
			URLMatcherResult urlMatcherResult, 
			WBWebPage wbWebPage, 
			WBProject project,
			Map<String, Object> model) throws WBException
	{

		if ((wbWebPage.getIsTemplateSource() == null) || (wbWebPage.getIsTemplateSource() == 0))
		{
			return wbWebPage.getHtmlSource();
		}
		
		if (model == null)
		{
			model = getControllerModel(request, urlMatcherResult, wbWebPage.getExternalKey(), project);
		}
		
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
				model.put(PAGE_CONTROLLER_MODEL_KEY, controllerModel);
			}			
		}
		String result = "";
		try {
			StringWriter out = new StringWriter();
			
			templateEngine.process(WBTemplateEngine.WEBPAGES_PATH_PREFIX + wbWebPage.getName(), model, out);
			result += out.toString();
		} catch (WBException e)
		{
			throw e;
		}
		
		return result;
	}

}
