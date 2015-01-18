package com.webpagebytes.cms.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.WPBPublicContentServlet;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.engine.WPBCacheInstances;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class FreeMarkerUriDirective implements TemplateDirectiveModel {
    private static final Logger log = Logger.getLogger(FreeMarkerArticleDirective.class.getName());
    WPBTemplateEngine templateEngine;
    WPBCacheInstances cacheInstances;
    String cache_query_param = WPBPublicContentServlet.CACHE_QUERY_PARAM;
    
    public void initialize(WPBTemplateEngine engine, WPBCacheInstances cacheInstances)
    {
        templateEngine = engine;
        this.cacheInstances = cacheInstances;
        
        CmsConfiguration configuration = CmsConfigurationFactory.getConfiguration();
        Map<String, String> generalParams = configuration.getSectionParams(WPBSECTION.SECTION_GENERAL);
        if (generalParams != null && generalParams.containsKey("cache_query_param"))
        {
            cache_query_param = generalParams.get("cache_query_param");
        }

    }
    
    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException 
    {
        String bodyStr = null;
        if (body != null)
        {
            StringWriter strWriter = new StringWriter();
            body.render(strWriter);
            bodyStr = strWriter.toString();
        }
        String uriPattern = null;
        if (params.containsKey("uriPattern"))
        {
            uriPattern = (String) DeepUnwrap.unwrap((TemplateModel) params.get("uriPattern"));
        } else
        {
            throw new TemplateModelException("FreeMarkerUriDirective does not have the uriPattern parameter set");
        }
        
        String uri = uriPattern;
        if (body != null)
        {
            StringWriter strWriter = new StringWriter();
            body.render(strWriter);
            uri = strWriter.toString().trim();
        }
        
        String uriFile = "";
        if (params.containsKey("uriFile"))
        {
            uriFile = (String) DeepUnwrap.unwrap((TemplateModel) params.get("uriFile"));
        }
        
        
        try
        {
            WPBUri wpbUri = cacheInstances.getUriCache().get(uriPattern, WPBUrisCache.HTTP_GET_INDEX);
            if (wpbUri == null)
            {
                log.log(Level.WARNING, "FreeMarkerUriDirective could not found WPBUri for " + uriPattern);      
            }
            if (wpbUri.getResourceType() == WPBUri.RESOURCE_TYPE_TEXT)
            {
                WPBPage page = cacheInstances.getPageCache().getByExternalKey(wpbUri.getResourceExternalKey());
                if (page != null && (page.getIsTemplateSource() == null || page.getIsTemplateSource() == 0))
                {
                    if (uri.indexOf("&")>0)
                    {
                        uri = uri.concat("&");
                    } else
                    {
                        uri = uri.concat("?");
                    }
                    uri = uri.concat(cache_query_param).concat("=").concat(page.getHash().toString());
                }
            }

            if (wpbUri.getResourceType() == WPBUri.RESOURCE_TYPE_FILE)
            {
                WPBFile file = cacheInstances.getFilesCache().geByPath(uriFile);
                if (file != null && (file.getDirectoryFlag()!=1))
                {
                    if (uri.indexOf("&")>0)
                    {
                        uri = uri.concat("&");
                    } else
                    {
                        uri = uri.concat("?");
                    }
                    uri = uri.concat(cache_query_param).concat("=").concat(file.getHash().toString());
                }
            }

            env.getOut().write(uri);
            
        } catch (WPBIOException e)
        {
            log.log(Level.SEVERE, "ERROR: ", e);
            throw new TemplateModelException("FreeMarkerUriDirective IO exception when handling: " + uriPattern);                   
        }
    }
    
}
