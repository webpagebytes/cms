package com.webbricks.template;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webbricks.cache.WBArticlesCache;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBWebPageModulesCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;

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
        
    	Long articleKey = null;
    	if (params.containsKey("externalKey"))
    	{
    		String articleKeyStr = (String) DeepUnwrap.unwrap((TemplateModel) params.get("externalKey"));
    		try
    		{
    			articleKey = Long.valueOf(articleKeyStr);
    		} catch (NumberFormatException e)
    		{
    			throw new TemplateModelException("WBFreeMarkerArticleDirective externalkey number format exception");
    		}
    	}
    	if (articleKey == null) throw new TemplateModelException("WBFreeMarkerArticleDirective does not have name parameter set");
    	
        try
        {
        	WBArticle article = cacheInstances.getWBArticleCache().get(articleKey);
        	if (article == null)
        	{
        		throw new TemplateModelException("WBFreeMarkerArticleDirective externalKey does not match an existing Article : " + articleKey);       
        	}
        	env.getOut().write(article.getHtmlSource());
        	
        } catch (WBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerArticleDirective IO exception when reading article: " + articleKey);               	
        }
    }
    

}
