package com.webpagebytes.cms;

import java.util.logging.Level;
import java.util.logging.Logger;



import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WBServletContextListener implements ServletContextListener {
	private static final Logger log = Logger.getLogger(WBServletContextListener.class.getName());
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// TODO Auto-generated method stub
		log.log(Level.INFO, "ServletContextListener:contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		// TODO Auto-generated method stub
		log.log(Level.INFO, "ServletContextListener:contextInitialized " + servletContextEvent.getServletContext().getContextPath());
	}

}
