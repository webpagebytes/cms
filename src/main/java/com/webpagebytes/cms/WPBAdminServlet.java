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

import javax.servlet.*;
import javax.servlet.http.*;

import com.webpagebytes.cms.engine.AdminRequestProcessorFactory;
import com.webpagebytes.cms.engine.AjaxRequestProcessor;
import com.webpagebytes.cms.engine.BaseRequestProcessorFactory;
import com.webpagebytes.cms.engine.ResourceRequestProcessor;
import com.webpagebytes.cms.engine.WPBServletUtility;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

public class WPBAdminServlet extends HttpServlet {

	public static final String ADMIN_URI_PREFIX = "admin-uri-prefix";
	public static final String ADMIN_RESOURCE_FOLDER = "admin";
	public static final String ADMIN_CONFIG_FOLDER = "config";
	public static final String ADMIN_CONFIG_RESOURCES = "META-INF/config/resourceswhitelist.properties";
	public static final String ADMIN_CONFIG_AJAX = "ajaxwhitelist.properties";
	
	
	private ResourceRequestProcessor resourceRequestProcessor = null;
	private AjaxRequestProcessor ajaxRequestProcssor = null;
	private AdminRequestProcessorFactory processorFactory = null;
	private WPBServletUtility servletUtility = null;
	
	private String adminURIPart = "";
	
	public WPBAdminServlet()
	{
		processorFactory = new BaseRequestProcessorFactory();
		servletUtility = new WPBServletUtility();
	}
	
	public void doAjax(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, java.io.IOException
    {
		try
		{
			String reqUri = getAdminRelativeUri(req);
			reqUri = decorateAdminRelativeUri(reqUri);
			if (ajaxRequestProcssor.isAjaxRequest(req, reqUri))
			{
				ajaxRequestProcssor.process(req, resp, reqUri);
			} else
			{
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (WPBException e)
		{
			throw new ServletException("WBGae - cound not complete request", e); 
		}
		
    }
	public String getAdminRelativeUri(HttpServletRequest req) throws ServletException
	{
		String reqUri = req.getRequestURI();
		if (reqUri.startsWith(adminURIPart))
		{
			reqUri = reqUri.substring(adminURIPart.length());
		} else
		{
			throw new ServletException("Cannot handle requests for uri that start with something different than " + adminURIPart);
		}
		return reqUri;
	}
	
	public String decorateAdminRelativeUri(String uri)
	{
		if (uri.endsWith("/"))
		{
			return uri + "index.html";
		} else
		if (uri.length() == 0)
		{
				return "/index.html";
		}
		return uri;
	}
	
	public void init() throws ServletException
    {
		// initialize the configFactory
		String configPath = servletUtility.getContextParameter(WPBCmsContextListener.CMS_CONFIG_KEY, this);
		if (null == configPath)
		{
			throw new ServletException("There is no wpbConfigurationPath parameter defined for admin context"); 
		}
		// WBConfigurationFactory.setConfigPath needs to be one of the first things to do for the servlet initialization
		// before at other code execution that relies on configurations
		if (CmsConfigurationFactory.getConfigPath() == null)
		{
			CmsConfigurationFactory.setConfigPath(configPath);
		}
		
		try
		{
			
		String strTemp =  servletUtility.getInitParameter(ADMIN_URI_PREFIX, this);
		
		if (null != strTemp && strTemp.length()>0)
		{
			if (strTemp.endsWith("/"))
			{
				adminURIPart = strTemp.substring(0, strTemp.length()-1);
			} else
			{
				adminURIPart = strTemp;
			}
		} else
		{
			throw new ServletException("There is no admin-prefix-uri parameter for the admin servlet configuration.");
		}
		} catch (Exception e)
		{
			throw new ServletException(e);
		}
		try
		{
			resourceRequestProcessor = processorFactory.createResourceRequestProcessor();
			resourceRequestProcessor.initialize(ADMIN_RESOURCE_FOLDER, ADMIN_CONFIG_RESOURCES);
	
			ajaxRequestProcssor = processorFactory.createAjaxRequestProcessor();
			ajaxRequestProcssor.initialize(ADMIN_CONFIG_FOLDER, ADMIN_CONFIG_AJAX);
			ajaxRequestProcssor.setAdminUriPart(adminURIPart);
		} catch (WPBException e)
		{
			throw new ServletException("WB Servlet initialization", e);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
     throws ServletException,
            java.io.IOException
            {
				try
				{
					String reqUri = getAdminRelativeUri(req);
					reqUri = decorateAdminRelativeUri(reqUri);
					if (ajaxRequestProcssor.isAjaxRequest(req, reqUri))
					{
						ajaxRequestProcssor.process(req, resp, reqUri);
						return;
					} else
					if (resourceRequestProcessor.isResourceRequest(reqUri))
					{
						resourceRequestProcessor.process(req, resp, reqUri);
						return;
					} else
					{
						resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					}
				} catch (WPBException e)
				{
					// to do - go to error handling
				}
			}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException,
           java.io.IOException
           {
				doAjax(req, resp);
           }

	public void doPut(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException,
           java.io.IOException
           {
				doAjax(req, resp);
           }

	public void doDelete(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException,
           java.io.IOException
           {
				doAjax(req, resp);
           }

	public ResourceRequestProcessor getResourceRequestProcessor() {
		return resourceRequestProcessor;
	}

	public void setResourceRequestProcessor(
			ResourceRequestProcessor resourceRequestProcessor) {
		this.resourceRequestProcessor = resourceRequestProcessor;
	}

	public String getAdminURIPart() {
		return adminURIPart;
	}

	public void setAdminURIPart(String adminURIPart) {
		this.adminURIPart = adminURIPart;
	}

	public AjaxRequestProcessor getAjaxRequestProcssor() {
		return ajaxRequestProcssor;
	}

	public void setAjaxRequestProcssor(AjaxRequestProcessor ajaxRequestProcssor) {
		this.ajaxRequestProcssor = ajaxRequestProcssor;
	}

	public AdminRequestProcessorFactory getProcessorFactory() {
		return processorFactory;
	}

	public void setProcessorFactory(AdminRequestProcessorFactory processorFactory) {
		this.processorFactory = processorFactory;
	}

	public WPBServletUtility getServletUtility() {
		return servletUtility;
	}

	public void setServletUtility(WPBServletUtility servletUtility) {
		this.servletUtility = servletUtility;
	}

	
	
	
	
}
