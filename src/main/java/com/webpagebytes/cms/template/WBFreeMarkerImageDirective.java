package com.webpagebytes.cms.template;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.datautility.WBBlobHandler;
import com.webpagebytes.cms.datautility.WBCloudFile;
import com.webpagebytes.cms.datautility.WBCloudFileStorage;
import com.webpagebytes.cms.exception.WBIOException;

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
	WBCloudFileStorage cloudFileStorage;
	
	public WBFreeMarkerImageDirective()
	{
		
	}
	public void initialize(WBCloudFileStorage cloudFileStorage, WBCacheInstances cacheInstances)
	{
		this.cacheInstances = cacheInstances;
		this.cloudFileStorage = cloudFileStorage;
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
    	   	
        try
        {
        	String serveUrl = "";
        	WBFile image = cacheInstances.getWBFilesCache().getByExternalKey(externalKey);
        	if (image != null)
        	{
        		WBCloudFile cloudFile = new WBCloudFile("public", image.getBlobKey());
        		serveUrl = cloudFileStorage.getPublicFileUrl(cloudFile);        	
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
