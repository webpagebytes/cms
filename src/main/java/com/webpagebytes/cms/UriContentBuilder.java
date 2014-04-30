package com.webpagebytes.cms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WBContentProvider;
import com.webpagebytes.cms.appinterfaces.WBRequestHandler;
import com.webpagebytes.cms.appinterfaces.WBForward;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.exception.WBException;

public class UriContentBuilder {

	private Map<String, WBRequestHandler> customControllers;
	private WBContentProvider contentProvider;
	
	UriContentBuilder(WBCacheInstances cacheInstances, ModelBuilder modelBuilder, 
			FileContentBuilder fileContentBuilder,
			PageContentBuilder pageContentBuilder)
	{
		customControllers = new HashMap<String, WBRequestHandler>();
		contentProvider = new WBDefaultContentProvider(fileContentBuilder, pageContentBuilder);
	}
	
	public void initialize()
	{
		
	}
	
	public void buildUriContent(HttpServletRequest request, HttpServletResponse response,
			WBUri wburi, 
			WBModel model,
			WBForward forward) throws WBException
	{
		String controllerClassName = wburi.getControllerClass();
		if (controllerClassName !=null && controllerClassName.length()>0)
		{
			WBRequestHandler controllerInst = null;
			if (customControllers.containsKey(controllerClassName))
			{
				controllerInst = customControllers.get(controllerClassName);
			} else
			{
				try {
				controllerInst = (WBRequestHandler) Class.forName(controllerClassName).newInstance();
				customControllers.put(controllerClassName, controllerInst);
				} catch (Exception e) { throw new WBException("Cannot instantiate page controller " + controllerClassName, e); }			
			}
			controllerInst.handleRequest(request, response, model, forward, contentProvider);
		}
	}

}
