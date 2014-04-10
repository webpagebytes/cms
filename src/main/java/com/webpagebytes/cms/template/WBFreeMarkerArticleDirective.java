package com.webpagebytes.cms.template;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.exception.WBIOException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class WBFreeMarkerArticleDirective implements TemplateDirectiveModel {
	private static final Logger log = Logger.getLogger(WBFreeMarkerArticleDirective.class.getName());
	WBTemplateEngine templateEngine;
	WBCacheInstances cacheInstances;

	public void initialize(WBTemplateEngine engine, WBCacheInstances cacheInstances)
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
        	WBArticle article = cacheInstances.getWBArticleCache().getByExternalKey(articleKeyStr);
        	if (article == null)
        	{
        		throw new TemplateModelException("WBFreeMarkerArticleDirective externalKey does not match an existing Article : " + articleKeyStr);       
        	}
        	env.getOut().write(article.getHtmlSource());
        	
        } catch (WBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerArticleDirective IO exception when reading article: " + articleKeyStr);               	
        }
    }
    

}
