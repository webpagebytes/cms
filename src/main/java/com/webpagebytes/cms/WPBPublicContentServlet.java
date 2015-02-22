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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.engine.DefaultWPBCacheFactory;
import com.webpagebytes.cms.engine.FileContentBuilder;
import com.webpagebytes.cms.engine.InternalModel;
import com.webpagebytes.cms.engine.LocalCloudFileContentBuilder;
import com.webpagebytes.cms.engine.ModelBuilder;
import com.webpagebytes.cms.engine.PageContentBuilder;
import com.webpagebytes.cms.engine.URLMatcher;
import com.webpagebytes.cms.engine.URLMatcherResult;
import com.webpagebytes.cms.engine.UriContentBuilder;
import com.webpagebytes.cms.engine.WPBCacheInstances;
import com.webpagebytes.cms.engine.WPBServletUtility;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.exception.WPBLocaleException;
import com.webpagebytes.cms.exception.WPBTemplateException;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

/**
 * <p>  
 * Class the extends HttpServlet to deliver the public content of Webpagebytes CMS.
 * </p>
 * <p>
 * A web application that integrates Webpagebytes CMS needs to define a servlet in the WEB-INF/web.xml 
 * for the content managed by the CMS.
 * </p>
 * <p>
 * An example of such web XML configuration fragment is the following <br>
 * <pre>
 * {@code
 * <servlet>
 *     <servlet-name>public</servlet-name>
 *     <servlet-class>com.webpagebytes.cms.WPBPublicContentServlet</servlet-class>
 * </servlet>  
 * <servlet-mapping>
 *    <servlet-name>public</servlet-name>
 *    <url-pattern>/*</url-pattern>
 * </servlet-mapping>
 * }
 * </pre>
 */
public class WPBPublicContentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(WPBPublicContentServlet.class.getName());
	public static final String CACHE_QUERY_PARAM = "cqp";
	public static final String CACHE_MAX_AGE = "31536000"; // it's one year in seconds
	public static final String CONTEXT_PATH = "wpb-context-path";

	public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_ETAG = "ETag";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    
    
    
	private WPBServletUtility servletUtility = null;
	
	private String uriCommonPrefix = ""; 
	
	private URLMatcher urlMatcherArray[] = new URLMatcher[4];
	private PageContentBuilder pageContentBuilder;
	private FileContentBuilder fileContentBuilder;
	private UriContentBuilder uriContentBuilder;
	WPBCacheFactory cacheFactory = DefaultWPBCacheFactory.getInstance();
	private WPBCacheInstances cacheInstances;
	private ModelBuilder modelBuilder;
	private String cache_query_param = CACHE_QUERY_PARAM;
	private String cache_max_age = CACHE_MAX_AGE;
	
public WPBPublicContentServlet()
{
	setServletUtility(new WPBServletUtility());		
}

public void initUrls() throws WPBIOException
{
	for(int i=0; i<4; i++)
	{
		Set<String> uris = cacheInstances.getUriCache().getAllUris(i);
		this.urlMatcherArray[i] = new URLMatcher();
		this.urlMatcherArray[i].initialize(uris, cacheInstances.getUriCache().getCacheFingerPrint());
	}	
}

public void initBuilders() throws WPBException
{
	modelBuilder = new ModelBuilder(cacheInstances);

	pageContentBuilder = new PageContentBuilder(cacheInstances, modelBuilder);
	pageContentBuilder.initialize();
	
	fileContentBuilder = new FileContentBuilder(cacheInstances);
	fileContentBuilder.initialize();
	
	uriContentBuilder = new UriContentBuilder(cacheInstances, modelBuilder, fileContentBuilder, pageContentBuilder);
	uriContentBuilder.initialize();	
}


public void init() throws ServletException
{
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
	
	cacheInstances = new WPBCacheInstances(cacheFactory.getUrisCacheInstance(), 
			cacheFactory.getWebPagesCacheInstance(), 
			cacheFactory.getPageModulesCacheInstance(), 
			cacheFactory.getParametersCacheInstance(),
			cacheFactory.getFilesCacheInstance(),
			cacheFactory.getArticlesCacheInstance(),
			cacheFactory.getMessagesCacheInstance(),
			cacheFactory.getProjectCacheInstance());

	CmsConfiguration configuration = CmsConfigurationFactory.getConfiguration();
	Map<String, String> generalParams = configuration.getSectionParams(WPBSECTION.SECTION_GENERAL);
	if ((generalParams != null) && generalParams.containsKey("cache_query_param"))
	{
	    cache_query_param = generalParams.get("cache_query_param");
	}
    if ((generalParams != null) && generalParams.containsKey("cache_max_age"))
    {
        try
        {
            cache_max_age = generalParams.get("cache_max_age");
            Integer.valueOf(cache_max_age);
        } catch (NumberFormatException e)
        {
            cache_max_age = CACHE_MAX_AGE;
        }
    }
	
	String initUriPrefix = servletUtility.getContextPath(this);
	if (initUriPrefix.length() > 0)
	{
		if (initUriPrefix.endsWith("/"))
		{
			initUriPrefix = initUriPrefix.substring(0, initUriPrefix.length()-1);
		}
		uriCommonPrefix = initUriPrefix;
	}
	
	try
	{
		initUrls();
		initBuilders();		
	} catch (Exception e)
	{
		log.log(Level.SEVERE, "ERROR: {0}", e);
		throw new ServletException(e);
	}
}
	
private URLMatcher getUrlMatcher(HttpServletRequest req) throws WPBIOException
{
	int currentHttpIndex = cacheInstances.getUriCache().httpToOperationIndex(req.getMethod().toUpperCase());
	URLMatcher urlMatcher = urlMatcherArray[currentHttpIndex];
	//reinitialize the matchurlToPattern if needed
	if (cacheInstances.getUriCache().getCacheFingerPrint().compareTo(urlMatcher.getFingerPrint())!= 0)
	{
		Set<String> allUris = cacheInstances.getUriCache().getAllUris(currentHttpIndex);
		urlMatcher.initialize(allUris, cacheInstances.getUriCache().getCacheFingerPrint());
	}
	return urlMatcher;
}
	
private void handleRequestTypeText(WPBPage webPage, HttpServletRequest req, HttpServletResponse resp, InternalModel model) throws WPBException, IOException
{
	if (webPage == null)
	{
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;
	}
	resp.setCharacterEncoding("UTF-8");
	Integer isTemplateSource = webPage.getIsTemplateSource();
	if (isTemplateSource != 1)
	{
	    String ifNoneMatch = req.getHeader(HEADER_IF_NONE_MATCH);
	    if (ifNoneMatch != null && ifNoneMatch.equals(webPage.getHash().toString()))
	    {
	        resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	        return;
	    }
	    resp.addHeader(HEADER_ETAG, webPage.getHash().toString());
		String cqp = req.getParameter(cache_query_param);
		if (cqp != null)
		{
			// this is a request that can be cached, to do customize the cache time
			resp.addHeader(HEADER_CACHE_CONTROL, "max-age=".concat(cache_max_age));
		}
	} else
	{
		resp.addHeader(HEADER_CACHE_CONTROL, "no-cache;no-store;");
	}
	byte[] content = pageContentBuilder.buildPageContent(req, webPage, model).getBytes("UTF-8");
	        
	        
	        
	resp.addHeader(HEADER_CONTENT_LENGTH, Integer.toString(content.length));
	resp.setContentType(webPage.getContentType());			
	ServletOutputStream os = resp.getOutputStream();
	os.write(content);
}

private void handleRequestTypeFile(String fileExternalKey, URLMatcherResult urlMatcherResult, HttpServletRequest req, HttpServletResponse resp) throws WPBException, IOException
{
	WPBFile wbFile = fileContentBuilder.find(fileExternalKey);
	if (wbFile == null)
	{
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;						
	}
	WPBFilesCache fileCache = cacheFactory.getFilesCacheInstance();
	WPBFile fileResponse = wbFile;
	if (wbFile.getDirectoryFlag() != null && wbFile.getDirectoryFlag() == 1)
	{
	    // this is a diretory match
	    // get the file path coresponding to the linked directory
	    String relativeFilePath = urlMatcherResult.getPatternParams().get("**");	    
	    String dirPath = fileCache.getFullFilePath(wbFile);
	    String sanitizedDirPath = "";
	    
	    if (relativeFilePath.startsWith("/")) relativeFilePath = relativeFilePath.substring(1);
	    if (dirPath != null && dirPath.length() > 0)
	    {
	        if (dirPath.endsWith("/")) sanitizedDirPath = dirPath.substring(0, dirPath.length()-1); 
	    }
	        
	    String fullFilePath = sanitizedDirPath + "/" + relativeFilePath;
	    
	    fileResponse = fileCache.geByPath(fullFilePath);
	    if (fileResponse == null)
	    {
	        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        return;                     
	    }	    	    
	}
	
    String ifNoneMatch = req.getHeader(HEADER_IF_NONE_MATCH);
    if (ifNoneMatch != null && ifNoneMatch.equals(fileResponse.getHash().toString()))
    {
        resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        return;
    }
    resp.addHeader(HEADER_ETAG, fileResponse.getHash().toString());
	String cqp = req.getParameter(cache_query_param);
	if (cqp != null)
	{
		// there is a request that can be cached
		resp.addHeader(HEADER_CACHE_CONTROL, "max-age=".concat(cache_max_age));
	}
	resp.addHeader(HEADER_CONTENT_LENGTH, fileResponse.getSize().toString());
	ServletOutputStream os = resp.getOutputStream();
	resp.setContentType(fileResponse.getAdjustedContentType());													
	fileContentBuilder.writeFileContent(fileResponse, os);
}

private void handleRequest(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException,
	java.io.IOException
{
	String uri = req.getRequestURI();
	if (uriCommonPrefix.length()>0 && uri.startsWith(uriCommonPrefix))
	{
		uri = uri.substring(uriCommonPrefix.length());
	}
	
	// urlMatcher will get the corresponding wbUri that macthes the current request 
	URLMatcher urlMatcher = null;
	req.setAttribute(CONTEXT_PATH, uriCommonPrefix);
	try
	{
		urlMatcher = getUrlMatcher(req);
	} catch (WPBIOException e)
	{
		//  nothing that can be done to serve better the request
		log.log(Level.SEVERE, "ERROR: ", e);
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return;
	}
	
	try
	{
		URLMatcherResult urlMatcherResult = urlMatcher.matchUrlToPattern(uri);
		if (urlMatcherResult == null)
		{
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		} else
		{
			int currentHttpIndex = cacheInstances.getUriCache().httpToOperationIndex(req.getMethod().toUpperCase());
			WPBUri wbUri = cacheInstances.getUriCache().get(urlMatcherResult.getUrlPattern(), currentHttpIndex);
			
			if ((null == wbUri) || (wbUri.getEnabled() == null) || (wbUri.getEnabled() == 0))
			{
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;					
			}
			
			// build the uri model
			InternalModel model = new InternalModel();
			WPBForward forward = new WPBForward();
			modelBuilder.populateModelForUriData(req, wbUri, urlMatcherResult, model);
			
			if (wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_URL_CONTROLLER)
			{
				uriContentBuilder.buildUriContent(req, resp, wbUri, model, forward);
				if (!forward.isRequestForwarded())
				{
					return;
				}
				// the request is forwarded to a page so we need to pass the same model
			}
			if (wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_TEXT || forward.isRequestForwarded())
			{
				WPBPage webPage = null;
				if (forward.isRequestForwarded())
				{
					webPage = pageContentBuilder.findWebPage(forward.getForwardTo());
				} else
				{					
					webPage = pageContentBuilder.findWebPage(wbUri.getResourceExternalKey());
				}
				handleRequestTypeText(webPage, req, resp, model);
			} else
			if (wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_FILE)
			{
				handleRequestTypeFile(wbUri.getResourceExternalKey(), urlMatcherResult, req, resp);
			} else
			{
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;										
			}
		} 
	}
	catch (WPBTemplateException e)
	{
		log.log(Level.SEVERE, "Template ERROR: ", e);
		ServletOutputStream os = resp.getOutputStream();
		String stack = Arrays.toString(e.getStackTrace());
		os.write(e.getMessage().getBytes("UTF-8")); os.write("\n".getBytes());				
		os.write(stack.getBytes("UTF-8"));
		os.write("-------------".getBytes());
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);				
	}
	catch (WPBLocaleException e)
	{
		// try to access a page with a locale that is not supported
		log.log(Level.SEVERE, "ERROR: ", e);
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return;				
	}
	catch (Exception e)
	{
		log.log(Level.SEVERE, "ERROR: ", e);
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return;
	}
	finally
	{
		// close the output stream
		OutputStream os = resp.getOutputStream();
		os.close();
	}
 }
	
public void doGet(HttpServletRequest req, HttpServletResponse resp)
 throws ServletException,
        java.io.IOException
        {
		handleRequest(req, resp);
		}

public void doPost(HttpServletRequest req, HttpServletResponse resp)
throws ServletException,
       java.io.IOException
       {
		handleRequest(req, resp);
       }

public void doPut(HttpServletRequest req, HttpServletResponse resp)
throws ServletException,
       java.io.IOException
       {
		handleRequest(req, resp);
       }

public void doDelete(HttpServletRequest req, HttpServletResponse resp)
throws ServletException,
       java.io.IOException
       {
		handleRequest(req, resp);
       }

public void doOptions(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
        java.io.IOException
        {
         handleRequest(req, resp);
        }

public void doHead(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
        java.io.IOException
        {
         handleRequest(req, resp);
        }

public void setServletUtility(WPBServletUtility servletUtility) {
	this.servletUtility = servletUtility;
}
	
	

}
