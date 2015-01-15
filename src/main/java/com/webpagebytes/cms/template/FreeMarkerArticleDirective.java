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

package com.webpagebytes.cms.template;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.engine.WPBCacheInstances;
import com.webpagebytes.cms.exception.WPBIOException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

class FreeMarkerArticleDirective implements TemplateDirectiveModel {
	private static final Logger log = Logger.getLogger(FreeMarkerArticleDirective.class.getName());
	WPBTemplateEngine templateEngine;
	WPBCacheInstances cacheInstances;

	public void initialize(WPBTemplateEngine engine, WPBCacheInstances cacheInstances)
	{
		templateEngine = engine;
		this.cacheInstances = cacheInstances;
	}
	
    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException 
    {
        // Check if no parameters were given:
    	if (body != null) throw new TemplateModelException("WBFreeMarkerArticleDirective does not suport directive body");
        
    	String articleKeyStr = null;
    	if (params.containsKey("externalKey"))
    	{
    		articleKeyStr = (String) DeepUnwrap.unwrap((TemplateModel) params.get("externalKey"));
    	} else
    	{
    		throw new TemplateModelException("WBFreeMarkerArticleDirective does not have external key parameter set");
    	}
    	
        try
        {
        	WPBArticle article = cacheInstances.getArticleCache().getByExternalKey(articleKeyStr);
        	if (article == null)
        	{
        		throw new TemplateModelException("WBFreeMarkerArticleDirective externalKey does not match an existing Article : " + articleKeyStr);       
        	}
        	env.getOut().write(article.getHtmlSource());
        	
        } catch (WPBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerArticleDirective IO exception when reading article: " + articleKeyStr);               	
        }
    }
    

}
