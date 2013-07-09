package com.webbricks.template;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBFilesCache;
import com.webbricks.cache.WBWebPageModulesCache;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.datautility.WBBlobHandler;
import com.webbricks.exception.WBIOException;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class WBFreeMarkerImageDirective implements TemplateDirectiveModel {
	private static final Logger log = Logger.getLogger(WBFreeMarkerModuleDirective.class.getName());
	WBCacheInstances cacheInstances;
	WBBlobHandler blobHandler;
	
	public WBFreeMarkerImageDirective()
	{
		
	}
	public void initialize(WBBlobHandler blobHandler, WBCacheInstances cacheInstances)
	{
		this.cacheInstances = cacheInstances;
		this.blobHandler = blobHandler;
	}
	
    public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException 
    {
        // Check if no parameters were given:
    	if (body != null) throw new TemplateModelException("WBFreeMarkerModuleDirective does not suport directive body");
        
    	String externalKey = null;
    	if (params.containsKey("externalKey"))
    	{
    		externalKey = (String) DeepUnwrap.unwrap((TemplateModel) params.get("externalKey"));
    	} else
    	{
    		throw new TemplateModelException("No external key for image directive");
    	}
    	
    	Long longExternalKey = null;
    	try
    	{
    		longExternalKey = Long.valueOf(externalKey);
    	} catch (NumberFormatException e)
    	{
    		throw new TemplateModelException("Format error for external key on image directive");
    	}
    	Integer size = 0;
    	if (params.containsKey("size"))
    	{
    		String strSize = (String) DeepUnwrap.unwrap((TemplateModel) params.get("size"));
    		try
    		{
    			size = Integer.valueOf(strSize);
    		} catch (NumberFormatException e)
    		{
    			// do nothing
    		}
    	}
    	
    	
        try
        {
        	String serveUrl = "";
        	WBFile image = cacheInstances.getWBImageCache().get(longExternalKey);
        	if (image != null)
        	{
        		serveUrl = blobHandler.serveBlobUrl(image.getBlobKey(), size);        	
        	} 
        	String htmlImage = "<img src=\"" + serveUrl + "\">";
        	env.getOut().write(htmlImage);
        	
        } catch (WBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerModuleDirective IO exception when reading image");               	
        }
    }

}
