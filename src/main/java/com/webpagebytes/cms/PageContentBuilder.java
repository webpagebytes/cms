package com.webpagebytes.cms;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.appinterfaces.IPageModelProvider;
import com.webpagebytes.appinterfaces.WBModel;
import com.webpagebytes.cache.WBCacheInstances;
import com.webpagebytes.cmsdata.WBProject;
import com.webpagebytes.cmsdata.WBWebPage;
import com.webpagebytes.exception.WBException;
import com.webpagebytes.template.WBFreeMarkerTemplateEngine;
import com.webpagebytes.template.WBTemplateEngine;

public class PageContentBuilder {
		
	private WBTemplateEngine templateEngine;
	private WBCacheInstances cacheInstances;
	private Map<String, IPageModelProvider> customControllers;
	private ModelBuilder modelBuilder;

	public PageContentBuilder(WBCacheInstances cacheInstances, ModelBuilder modelBuilder)
							
	{
		this.customControllers = new HashMap<String, IPageModelProvider>();
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
		
	private IPageModelProvider getPageModelProvider(String controllerClassName) throws WBException
	{
		IPageModelProvider controllerInst = null;
		if (customControllers.containsKey(controllerClassName))
		{
			controllerInst = (IPageModelProvider) customControllers.get(controllerClassName);
		} else
		{
			try {
			controllerInst = (IPageModelProvider) Class.forName(controllerClassName).newInstance();
			customControllers.put(controllerClassName, controllerInst);
			} catch (Exception e) { throw new WBException("Cannot instantiate page controller " + controllerClassName, e); }			
		}
		return controllerInst;
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

			IPageModelProvider controllerInst = getPageModelProvider(controllerClassName);
			controllerInst.getPageModel(request, model);
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
