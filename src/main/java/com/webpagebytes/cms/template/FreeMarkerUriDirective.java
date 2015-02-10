/*
 *   Copyright 2015 Webpagebytes
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
            bodyStr = strWriter.toString().trim();
        }
        String uriPattern = null;
        if (params.containsKey("uriPattern"))
        {
            uriPattern = (String) DeepUnwrap.unwrap((TemplateModel) params.get("uriPattern"));
        } 
                
        String uriFile = null;
        if (params.containsKey("uriFile"))
        {
            uriFile = (String) DeepUnwrap.unwrap((TemplateModel) params.get("uriFile"));
            if (uriFile.startsWith("/"))
            {
                uriFile = uriFile.substring(1);
            }
        }
        
        if (uriFile == null && uriPattern == null)
        {
            throw new TemplateModelException("FreeMarkerUriDirective does not have the uriPattern or uriFile parameter set");
        }
        // decide a priority for considering the uri. Low priority is the uriFile, medium is the uriPattern
        // higher is the bodyStr
        String uri = uriFile;
        if (uriPattern != null)
        {
            uri = uriPattern; 
        }
        if (body != null)
        {         
            uri = bodyStr;
        }

        
        try
        {
            if (uriFile != null)
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
            } else
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
            }

            env.getOut().write(uri);
            
        } catch (WPBIOException e)
        {
            log.log(Level.SEVERE, "ERROR: ", e);
            throw new TemplateModelException("FreeMarkerUriDirective IO exception when handling: " + uriPattern);                   
        }
    }
    
}
