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
import com.webbricks.appinterfaces.WBModel;
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

public class PageContentBuilder {
		
	private WBTemplateEngine templateEngine;
	private WBCacheInstances cacheInstances;
	private Map<String, Object> customControllers;
	private ModelBuilder modelBuilder;

	public PageContentBuilder(WBCacheInstances cacheInstances, ModelBuilder modelBuilder)
							
	{
		this.customControllers = new HashMap<String, Object>();
		this.cacheInstances = cacheInstances;
		this.modelBuilder = modelBuilder;
		this.templateEngine = new WBFreeMarkerTemplateEngine(cacheInstances);
		
	}
	
	public void initialize() throws WBException
	{
			templateEngine.initialize();
	}
	
	public WBWebPage findWebPage(String pageExternalKey) throws WBException
	{
		return cacheInstances.getWBWebPageCache().getByExternalKey(pageExternalKey);		
	}
		
	
	public String buildPageContent(HttpServletRequest request,
			WBWebPage wbWebPage, 
			WBProject project,
			WBModel model) throws WBException
	{

		Integer istemplateSource = wbWebPage.getIsTemplateSource();
		if (istemplateSource == null || istemplateSource == 0)
		{
			return wbWebPage.getHtmlSource();
		}
						
		modelBuilder.populateModelForWebPage(request, wbWebPage, model);
		
		String controllerClassName = wbWebPage.getPageModelProvider();

		Map<String, Object> rootModel = new HashMap<String, Object>();
		
		boolean hasController = controllerClassName !=null && controllerClassName.length()>0;
		
		if (hasController)
		{

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
				controllerInst.getPageModel(request, model);
			}			
		}
		model.transferModel(rootModel);
		rootModel.put(ModelBuilder.PAGE_CONTROLLER_MODEL_KEY, model.getCmsCustomModel());
		
		if (model.getCmsModel().containsKey(ModelBuilder.LOCALE_KEY))
		{
			rootModel.put(ModelBuilder.LOCALE_COUNTRY_KEY, model.getCmsModel().get(ModelBuilder.LOCALE_KEY).get(ModelBuilder.LOCALE_COUNTRY_KEY));
			rootModel.put(ModelBuilder.LOCALE_LANGUAGE_KEY, model.getCmsModel().get(ModelBuilder.LOCALE_KEY).get(ModelBuilder.LOCALE_LANGUAGE_KEY));
		}
		
		String result = "";
		try {
			StringWriter out = new StringWriter();			
			templateEngine.process(WBTemplateEngine.WEBPAGES_PATH_PREFIX + wbWebPage.getName(), rootModel, out);
			result += out.toString();
		} catch (WBException e)
		{
			throw e;
		}
		
		return result;
	}

}
