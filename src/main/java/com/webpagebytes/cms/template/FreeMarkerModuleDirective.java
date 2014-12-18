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
import java.util.logging.Logger;

import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.engine.WPBCacheInstances;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBTemplateException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class FreeMarkerModuleDirective extends FreeMarkerDirectiveBase {
	private static final Logger log = Logger.getLogger(FreeMarkerModuleDirective.class.getName());
	WPBTemplateEngine templateEngine;
	WPBCacheInstances cacheInstances;
	
	public FreeMarkerModuleDirective()
	{
		
	}
	public void initialize(WPBTemplateEngine engine, WPBCacheInstances cacheInstances)
	{
		this.templateEngine = engine;
		this.cacheInstances = cacheInstances;
	}
	
    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException 
    {
        // Check if no parameters were given:
    	if (body != null) throw new TemplateModelException("WBFreeMarkerModuleDirective does not suport directive body");
        
    	copyParams(env, params);
    	
    	String externalKey = null;
    	if (params.containsKey("externalKey"))
    	{
    		externalKey = (String) DeepUnwrap.unwrap((TemplateModel) params.get("externalKey"));
    	}
    	if (externalKey == null) throw new TemplateModelException("WBFreeMarkerModuleDirective does not have externalKey parameter set");
    	
        try
        {
        	WPBPageModule pageModule = cacheInstances.getWBWebPageModuleCache().getByExternalKey(externalKey);
        	if (pageModule == null)
        	{
        		throw new TemplateModelException("WBFreeMarkerModuleDirective directive name does not match any existing page module: " + externalKey);       
        	}
        	if (pageModule.getIsTemplateSource() == 1)
        	{
        		String moduleName = WPBTemplateEngine.WEBMODULES_PATH_PREFIX + pageModule.getExternalKey();
        	    templateEngine.process(moduleName, params, env.getOut());        	 
        	} else
        	{
        		env.getOut().write(pageModule.getHtmlSource());
        	}
        } 
        catch (WPBTemplateException e)
        {
        	String message = "WBFreeMarkerModuleDirective template exception when reading page module: " + externalKey;
        	message += "\n";
        	message += e.getMessage();
        	throw new TemplateModelException(message);
        }
        catch (WPBException e)
        {
        	throw new TemplateModelException("WBFreeMarkerModuleDirective exception when reading page module: " + externalKey);               	
        }
    }

	   
}
