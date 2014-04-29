package com.webpagebytes.cms;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.appinterfaces.WBPageModelProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.template.WBFreeMarkerTemplateEngine;
import com.webpagebytes.cms.template.WBTemplateEngine;

public class PageContentBuilder {
		
	private WBTemplateEngine templateEngine;
	private WBCacheInstances cacheInstances;
	private Map<String, WBPageModelProvider> customControllers;
	private ModelBuilder modelBuilder;

	public PageContentBuilder(WBCacheInstances cacheInstances, ModelBuilder modelBuilder)
							
	{
		this.customControllers = new HashMap<String, WBPageModelProvider>();
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
		
	private WBPageModelProvider getPageModelProvider(String controllerClassName) throws WBException
	{
		WBPageModelProvider controllerInst = null;
		if (customControllers.containsKey(controllerClassName))
		{
			controllerInst = (WBPageModelProvider) customControllers.get(controllerClassName);
		} else
		{
			try {
			controllerInst = (WBPageModelProvider) Class.forName(controllerClassName).newInstance();
			customControllers.put(controllerClassName, controllerInst);
			} catch (Exception e) { throw new WBException("Cannot instantiate page controller " + controllerClassName, e); }			
		}
		return controllerInst;
	}
	
	public String buildPageContent(HttpServletRequest request,
			WBWebPage wbWebPage, 
			WBModel model) throws WBException
	{

		Integer istemplateSource = wbWebPage.getIsTemplateSource();
		if (istemplateSource == null || istemplateSource == 0)
		{
			return wbWebPage.getHtmlSource();
		}
						
		modelBuilder.populateModelForWebPage(wbWebPage, model);
		
		String controllerClassName = wbWebPage.getPageModelProvider();

		Map<String, Object> rootModel = new HashMap<String, Object>();
		
		boolean hasController = controllerClassName!=null && controllerClassName.length()>0;
		
		if (hasController)
		{
			WBPageModelProvider controllerInst = getPageModelProvider(controllerClassName);
			controllerInst.populatePageModel(request, model);
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

	public String buildPageContent(
			WBWebPage wbWebPage, 
			WBModel model) throws WBException
	{

		Integer istemplateSource = wbWebPage.getIsTemplateSource();
		if (istemplateSource == null || istemplateSource == 0)
		{
			return wbWebPage.getHtmlSource();
		}
						
		modelBuilder.populateModelForWebPage(wbWebPage, model);
		
		String controllerClassName = wbWebPage.getPageModelProvider();

		Map<String, Object> rootModel = new HashMap<String, Object>();
		
		boolean hasController = controllerClassName!=null && controllerClassName.length()>0;
		
		if (hasController)
		{
			WBPageModelProvider controllerInst = getPageModelProvider(controllerClassName);
			controllerInst.populatePageModel(model);
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
