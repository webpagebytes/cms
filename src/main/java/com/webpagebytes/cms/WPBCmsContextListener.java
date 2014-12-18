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

package com.webpagebytes.cms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

public class WPBCmsContextListener implements ServletContextListener {
	public static final String CMS_CONFIG_KEY = "wpbConfigurationPath";

	private static final Logger log = Logger.getLogger(WPBCmsContextListener.class.getName());
	
	public void contextDestroyed(ServletContextEvent servletContext) {
		
		
	}

	public void contextInitialized(ServletContextEvent servletContext) {
		
    	log.log(Level.INFO, "WBCmsContextListener context initialized");
    	String configPath = servletContext.getServletContext().getInitParameter(CMS_CONFIG_KEY);
    	if (null == configPath)
    	{
    		throw new RuntimeException("There is no wpbConfigurationPath parameter defined on WBCmsContextListener context initialized "); 
    	}
    	// WBConfigurationFactory.setConfigPath needs to be one of the first things to do for the servlet initialization
    	// before at other code execution that relies on configurations
    	if (CmsConfigurationFactory.getConfigPath() == null)
    	{
    		CmsConfigurationFactory.setConfigPath(configPath);
    	}

		
	}

}
