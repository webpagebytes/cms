package com.webpagebytes.cms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.appinterfaces.IWBRequestHandler;
import com.webpagebytes.appinterfaces.WBForward;
import com.webpagebytes.appinterfaces.WBModel;
import com.webpagebytes.cache.WBCacheInstances;
import com.webpagebytes.cmsdata.WBUri;
import com.webpagebytes.exception.WBException;

public class UriContentBuilder {

	private Map<String, IWBRequestHandler> customControllers;
	
	UriContentBuilder(WBCacheInstances cacheInstances, ModelBuilder modelBuilder)
	{
		customControllers = new HashMap<String, IWBRequestHandler>();
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
			IWBRequestHandler controllerInst = null;
			if (customControllers.containsKey(controllerClassName))
			{
				controllerInst = customControllers.get(controllerClassName);
			} else
			{
				try {
				controllerInst = (IWBRequestHandler) Class.forName(controllerClassName).newInstance();
				customControllers.put(controllerClassName, controllerInst);
				} catch (Exception e) { throw new WBException("Cannot instantiate page controller " + controllerClassName, e); }			
			}
			controllerInst.handleRequest(request, response, model, forward);
		}
	}

}
