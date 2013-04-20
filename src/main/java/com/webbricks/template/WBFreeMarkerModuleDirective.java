package com.webbricks.template;

import java.io.IOException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBWebPageModuleCache;
import com.webbricks.cms.PageContentBuilder;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class WBFreeMarkerModuleDirective extends WBFreeMarkerDirectiveBase {
	private static final Logger log = Logger.getLogger(WBFreeMarkerModuleDirective.class.getName());
	WBTemplateEngine templateEngine;
	WBCacheInstances cacheInstances;
	
	public WBFreeMarkerModuleDirective()
	{
		
	}
	public void initialize(WBTemplateEngine engine, WBCacheInstances cacheInstances)
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
    	
    	String moduleName = null;
    	if (params.containsKey("name"))
    	{
    		moduleName = (String) DeepUnwrap.unwrap((TemplateModel) params.get("name"));
    	}
    	if (moduleName == null) throw new TemplateModelException("WBFreeMarkerModuleDirective does not have name parameter set");
    	
        try
        {
        	WBWebPageModule pageModule = cacheInstances.getWBWebPageModuleCache().get(moduleName);
        	if (pageModule == null)
        	{
        		throw new TemplateModelException("WBFreeMarkerModuleDirective directive name does not match any existing page module: " + moduleName);       
        	}
        	if (pageModule.getIsTemplateSource() == 1)
        	{
        		moduleName = WBTemplateEngine.WEBMODULES_PATH_PREFIX + moduleName;
        	    templateEngine.process(moduleName, params, env.getOut());        	 
        	} else
        	{
        		env.getOut().write(pageModule.getHtmlSource());
        	}
        } catch (WBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerModuleDirective IO exception when reading page module: " + moduleName);               	
        }
    }

	   
}
