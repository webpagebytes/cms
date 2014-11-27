package com.webpagebytes.cms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBContentProvider;
import com.webpagebytes.cms.appinterfaces.WPBRequestHandler;
import com.webpagebytes.cms.appinterfaces.WPBForward;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.exception.WPBException;

class UriContentBuilder {

	private Map<String, WPBRequestHandler> customControllers;
	private WPBContentProvider contentProvider;
	
	UriContentBuilder(WPBCacheInstances cacheInstances, ModelBuilder modelBuilder, 
			FileContentBuilder fileContentBuilder,
			PageContentBuilder pageContentBuilder)
	{
		customControllers = new HashMap<String, WPBRequestHandler>();
		contentProvider = new WPBDefaultContentProvider(fileContentBuilder, pageContentBuilder);
	}
	
	public void initialize()
	{
		
	}
	
	public void buildUriContent(HttpServletRequest request, HttpServletResponse response,
			WPBUri wburi, 
			WPBModel model,
			WPBForward forward) throws WPBException
	{
		String controllerClassName = wburi.getControllerClass();
		if (controllerClassName !=null && controllerClassName.length()>0)
		{
			WPBRequestHandler controllerInst = null;
			if (customControllers.containsKey(controllerClassName))
			{
				controllerInst = customControllers.get(controllerClassName);
			} else
			{
				try {
				controllerInst = (WPBRequestHandler) Class.forName(controllerClassName).newInstance();
				controllerInst.initialize(contentProvider);
				customControllers.put(controllerClassName, controllerInst);
				} catch (Exception e) { throw new WPBException("Cannot instantiate page controller " + controllerClassName, e); }			
			}
			controllerInst.handleRequest(request, response, model, forward);
		}
	}

}
