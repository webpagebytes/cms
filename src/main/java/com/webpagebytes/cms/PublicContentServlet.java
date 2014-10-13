package com.webpagebytes.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WBForward;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.DefaultWBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.exception.WBLocaleException;
import com.webpagebytes.cms.exception.WBTemplateException;
import com.webpagebytes.cms.utility.WBConfigurationFactory;

public class PublicContentServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(PublicContentServlet.class.getName());
	public static final String CACHE_QUERY_PARAM = "cqp";
	public static final String URI_PREFIX = "wb-uri-prefix";
	public static final String CMS_CONFIG_KEY = "wpbConfigurationPath";

	private WBServletUtility servletUtility = null;
	
	// this is the common uri part that will be common to all requests served by this CMS
	// corresponds to uri-prefix init parameter.
	
	private String uriCommonPrefix = ""; 
	
	private URLMatcher urlMatcherArray[] = new URLMatcher[4];
	private LocalCloudFileContentBuilder localFileContentBuilder;
	private PageContentBuilder pageContentBuilder;
	private FileContentBuilder fileContentBuilder;
	private UriContentBuilder uriContentBuilder;
	private WBCacheInstances cacheInstances;
	private ModelBuilder modelBuilder;
	
	public PublicContentServlet()
	{
		setServletUtility(new WBServletUtility());		
	}
	
	public void init(ServletConfig config) throws ServletException
    {
		super.init(config);
		
		String configPath = servletUtility.getContextParameter(CMS_CONFIG_KEY, this);
		if (null == configPath)
		{
			throw new ServletException("There is no wpbConfigurationPath parameter defined for admin context"); 
		}
		if (WBConfigurationFactory.getConfigPath() == null)
		{
			WBConfigurationFactory.setConfigPath(configPath);
		}

		localFileContentBuilder = new LocalCloudFileContentBuilder();
		WBCacheFactory wbCacheFactory = DefaultWBCacheFactory.getInstance();
		this.cacheInstances = new WBCacheInstances(wbCacheFactory.createWBUrisCacheInstance(), 
				wbCacheFactory.createWBWebPagesCacheInstance(), 
				wbCacheFactory.createWBWebPageModulesCacheInstance(), 
				wbCacheFactory.createWBParametersCacheInstance(),
				wbCacheFactory.createWBImagesCacheInstance(),
				wbCacheFactory.createWBArticlesCacheInstance(),
				wbCacheFactory.createWBMessagesCacheInstance(),
				wbCacheFactory.createWBProjectCacheInstance());

		
		String initUriPrefix = servletUtility.getInitParameter("uri-prefix", this);
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
			
			for(int i=0; i<4; i++)
			{
				Set<String> uris = cacheInstances.getWBUriCache().getAllUris(i);
				this.urlMatcherArray[i] = new URLMatcher();
				this.urlMatcherArray[i].initialize(uris, cacheInstances.getWBUriCache().getCacheFingerPrint());
			}
			
			modelBuilder = new ModelBuilder(cacheInstances);

			pageContentBuilder = new PageContentBuilder(cacheInstances, modelBuilder);
			pageContentBuilder.initialize();
			
			fileContentBuilder = new FileContentBuilder(cacheInstances);
			fileContentBuilder.initialize();
			
			uriContentBuilder = new UriContentBuilder(cacheInstances, modelBuilder, fileContentBuilder, pageContentBuilder);
			uriContentBuilder.initialize();

			
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "ERROR: {0}", e);
			throw new ServletException(e);
		}
    }
	
	private URLMatcher getUrlMatcher(HttpServletRequest req)
	{
		int currentHttpIndex = cacheInstances.getWBUriCache().httpToOperationIndex(req.getMethod().toUpperCase());
		URLMatcher urlMatcher = urlMatcherArray[currentHttpIndex];
		//reinitialize the matchurlToPattern if needed
		if (cacheInstances.getWBUriCache().getCacheFingerPrint().compareTo(urlMatcher.getFingerPrint())!= 0)
		{
			try
			{
				Set<String> allUris = cacheInstances.getWBUriCache().getAllUris(currentHttpIndex);
				urlMatcher.initialize(allUris, cacheInstances.getWBUriCache().getCacheFingerPrint());
			} catch (WBIOException e)
			{
				log.log(Level.SEVERE, "Could not reinitialize the URL matcher {0} ", e);
				// do not fail as some urls may still work
			}
		}
		return urlMatcher;
	}
	
	private void handleRequestTypeText(WBWebPage webPage, HttpServletRequest req, HttpServletResponse resp, WBModel model) throws WBException, IOException
	{
		if (webPage == null)
		{
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String content = pageContentBuilder.buildPageContent(req, webPage, model);
		resp.setCharacterEncoding("UTF-8");
		Integer isTemplateSource = webPage.getIsTemplateSource();
		if (isTemplateSource != 1)
		{
			String cqp = req.getParameter(CACHE_QUERY_PARAM);
			if (cqp != null && cqp.equals(webPage.getHash().toString()))
			{
				// this is a request that can be cached, to do customize the cache time
				resp.addHeader("cache-control", "max-age=86400");
			}
		} else
		{
			resp.addHeader("cache-control", "no-cache;no-store;");
		}
		resp.setContentType(webPage.getContentType());			
		ServletOutputStream os = resp.getOutputStream();
		os.write(content.getBytes("UTF-8"));
	}

	private void handleRequestTypeFile(String fileExternalKey, HttpServletRequest req, HttpServletResponse resp) throws WBException, IOException
	{
		WBFile wbFile = fileContentBuilder.find(fileExternalKey);
		if (wbFile == null)
		{
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;						
		}
		String cqp = req.getParameter(CACHE_QUERY_PARAM);
		if (cqp != null && cqp.equals(wbFile.getHash().toString()))
		{
			// there is a request that can be cached
			resp.addHeader("cache-control", "max-age=86400");
		}
		ServletOutputStream os = resp.getOutputStream();
		resp.setContentType(wbFile.getAdjustedContentType());													
		fileContentBuilder.writeFileContent(wbFile, os);
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
		
		req.setAttribute(URI_PREFIX, uriCommonPrefix);
		
		URLMatcher urlMatcher = getUrlMatcher(req);
		
		URLMatcherResult urlMatcherResult = urlMatcher.matchUrlToPattern(uri);
		if (urlMatcherResult == null)
		{
			if (uri.startsWith(LocalCloudFileContentBuilder.LOCAL_FILE_SERVE_URL))
			{
				try
				{
					localFileContentBuilder.serveFile(req, resp, uri);
				} catch (WBIOException e)
				{
					resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);				
				}
				return;
			}
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		} else
		{
			try
			{
				int currentHttpIndex = cacheInstances.getWBUriCache().httpToOperationIndex(req.getMethod().toUpperCase());
				WBUri wbUri = cacheInstances.getWBUriCache().get(urlMatcherResult.getUrlPattern(), currentHttpIndex);
				
				if ((null == wbUri) || (wbUri.getEnabled() == null) || (wbUri.getEnabled() == 0))
				{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;					
				}
				
				// build the uri model
				WBModel model = new WBModel();
				WBForward forward = new WBForward();
				modelBuilder.populateModelForUriData(req, wbUri, urlMatcherResult, model);
				
				if (wbUri.getResourceType() == WBUri.RESOURCE_TYPE_URL_CONTROLLER)
				{
					uriContentBuilder.buildUriContent(req, resp, wbUri, model, forward);
					if (!forward.isRequestForwarded())
					{
						return;
					}
					// the request is forwarded to a page so we need to pass the same model
				}
				if (wbUri.getResourceType() == WBUri.RESOURCE_TYPE_TEXT || forward.isRequestForwarded())
				{
					WBWebPage webPage = null;
					if (forward.isRequestForwarded())
					{
						webPage = pageContentBuilder.findWebPage(forward.getForwardTo());
					} else
					{					
						webPage = pageContentBuilder.findWebPage(wbUri.getResourceExternalKey());
					}
					handleRequestTypeText(webPage, req, resp, model);
				} else
				if (wbUri.getResourceType() == WBUri.RESOURCE_TYPE_FILE)
				{
					handleRequestTypeFile(wbUri.getResourceExternalKey(), req, resp);
				} else
				{
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;										
				}
			} 
			catch (WBTemplateException e)
			{
				log.log(Level.SEVERE, "Template ERROR: ", e);
				ServletOutputStream os = resp.getOutputStream();
				String stack = Arrays.toString(e.getStackTrace());
				os.write(e.getMessage().getBytes("UTF-8")); os.write("\n".getBytes());				
				os.write(stack.getBytes("UTF-8"));
				os.write("-------------".getBytes());
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);				
			}
			catch (WBLocaleException e)
			{
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

	public void setServletUtility(WBServletUtility servletUtility) {
		this.servletUtility = servletUtility;
	}
	
	

}
