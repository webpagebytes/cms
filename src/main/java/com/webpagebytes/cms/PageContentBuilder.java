package com.webpagebytes.cms;


import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.webpagebytes.cms.appinterfaces.WPBPageModelProvider;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.template.WPBFreeMarkerTemplateEngine;
import com.webpagebytes.cms.template.WPBTemplateEngine;

class PageContentBuilder {
		
	private WPBTemplateEngine templateEngine;
	private WPBCacheInstances cacheInstances;
	private Map<String, WPBPageModelProvider> customControllers;
	private ModelBuilder modelBuilder;

	public PageContentBuilder(WPBCacheInstances cacheInstances, ModelBuilder modelBuilder)
							
	{
		this.customControllers = new HashMap<String, WPBPageModelProvider>();
		this.cacheInstances = cacheInstances;
		this.modelBuilder = modelBuilder;
		this.templateEngine = new WPBFreeMarkerTemplateEngine(cacheInstances);
		
	}
	
	public void initialize() throws WPBException
	{
			templateEngine.initialize();
	}
	
	public WBWebPage findWebPage(String pageExternalKey) throws WPBException
	{
		return cacheInstances.getWBWebPageCache().getByExternalKey(pageExternalKey);		
	}
		
	private WPBPageModelProvider getPageModelProvider(String controllerClassName) throws WPBException
	{
		WPBPageModelProvider controllerInst = null;
		if (customControllers.containsKey(controllerClassName))
		{
			controllerInst = (WPBPageModelProvider) customControllers.get(controllerClassName);
		} else
		{
			try {
			controllerInst = (WPBPageModelProvider) Class.forName(controllerClassName).newInstance();
			customControllers.put(controllerClassName, controllerInst);
			} catch (Exception e) { throw new WPBException("Cannot instantiate page controller " + controllerClassName, e); }			
		}
		return controllerInst;
	}
	
	public String buildPageContent(HttpServletRequest request,
			WBWebPage wbWebPage, 
			WPBModel model) throws WPBException
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
			WPBPageModelProvider controllerInst = getPageModelProvider(controllerClassName);
			controllerInst.populatePageModel(model);
		}
		model.transferModel(rootModel);
		rootModel.put(WPBModel.APPLICATION_CONTROLLER_MODEL_KEY, model.getCmsCustomModel());
		
		if (model.getCmsModel().containsKey(WPBModel.LOCALE_KEY))
		{
			rootModel.put(WPBModel.LOCALE_COUNTRY_KEY, model.getCmsModel().get(WPBModel.LOCALE_KEY).get(WPBModel.LOCALE_COUNTRY_KEY));
			rootModel.put(WPBModel.LOCALE_LANGUAGE_KEY, model.getCmsModel().get(WPBModel.LOCALE_KEY).get(WPBModel.LOCALE_LANGUAGE_KEY));
		}
		
		String result = "";
		try {
			StringWriter out = new StringWriter();			
			templateEngine.process(WPBTemplateEngine.WEBPAGES_PATH_PREFIX + wbWebPage.getName(), rootModel, out);
			result += out.toString();
		} catch (WPBException e)
		{
			throw e;
		}
		
		return result;
	}

	public String buildPageContent(
			WBWebPage wbWebPage, 
			WPBModel model) throws WPBException
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
			WPBPageModelProvider controllerInst = getPageModelProvider(controllerClassName);
			controllerInst.populatePageModel(model);
		}
		model.transferModel(rootModel);
		rootModel.put(WPBModel.APPLICATION_CONTROLLER_MODEL_KEY, model.getCmsCustomModel());
		
		if (model.getCmsModel().containsKey(WPBModel.LOCALE_KEY))
		{
			rootModel.put(WPBModel.LOCALE_COUNTRY_KEY, model.getCmsModel().get(WPBModel.LOCALE_KEY).get(WPBModel.LOCALE_COUNTRY_KEY));
			rootModel.put(WPBModel.LOCALE_LANGUAGE_KEY, model.getCmsModel().get(WPBModel.LOCALE_KEY).get(WPBModel.LOCALE_LANGUAGE_KEY));
		}
		
		String result = "";
		try {
			StringWriter out = new StringWriter();			
			templateEngine.process(WPBTemplateEngine.WEBPAGES_PATH_PREFIX + wbWebPage.getName(), rootModel, out);
			result += out.toString();
		} catch (WPBException e)
		{
			throw e;
		}
		
		return result;
	}

}
