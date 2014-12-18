/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.engine;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.WPBContentProvider;
import com.webpagebytes.cms.WPBForward;
import com.webpagebytes.cms.WPBModel;
import com.webpagebytes.cms.WPBRequestHandler;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.exception.WPBException;

public class UriContentBuilder {

	private Map<String, WPBRequestHandler> customControllers;
	private WPBContentProvider contentProvider;
	
	public UriContentBuilder(WPBCacheInstances cacheInstances, ModelBuilder modelBuilder, 
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
