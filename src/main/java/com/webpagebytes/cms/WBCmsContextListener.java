package com.webpagebytes.cms;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.webpagebytes.cms.utility.WBConfigurationFactory;

public class WBCmsContextListener implements ServletContextListener {
	public static final String CMS_CONFIG_KEY = "wpbConfigurationPath";

	private static final Logger log = Logger.getLogger(WBCmsContextListener.class.getName());
	
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
    	if (WBConfigurationFactory.getConfigPath() == null)
    	{
    		WBConfigurationFactory.setConfigPath(configPath);
    	}

		
	}

}
