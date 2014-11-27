package com.webpagebytes.cms.template;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.datautility.WPBCloudFile;
import com.webpagebytes.cms.datautility.WPBCloudFileStorage;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.utility.WBBase64Utility;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class WBFreeMarkerImageDirective implements TemplateDirectiveModel {
	private static final Logger log = Logger.getLogger(WBFreeMarkerModuleDirective.class.getName());
	WPBCacheInstances cacheInstances;
	WPBCloudFileStorage cloudFileStorage;
	
	public WBFreeMarkerImageDirective()
	{
		
	}
	public void initialize(WPBCloudFileStorage cloudFileStorage, WPBCacheInstances cacheInstances)
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
    	
    	boolean embedded = false;
    	if (params.containsKey("embedded"))
    	{
    		String embeddedStr = (String) DeepUnwrap.unwrap((TemplateModel) params.get("embedded"));
    		embedded = embeddedStr.toLowerCase().equals("true");
    	}
        try
        {
        	String serveUrl = "";
        	WBFile image = cacheInstances.getWBFilesCache().getByExternalKey(externalKey);
        	if (image == null)
        	{
        		log.log(Level.WARNING, "cannot find iamge with key" + externalKey);
        		return;
        	}
        	WPBCloudFile cloudFile = new WPBCloudFile("public", image.getBlobKey());
        	if (! embedded)
        	{
        		serveUrl = cloudFileStorage.getPublicFileUrl(cloudFile);        	
        		env.getOut().write(serveUrl);
        	} else
        	{
        		
        		InputStream is = null;
        		ByteArrayOutputStream baos = null;
        		try
        		{
	        		is = cloudFileStorage.getFileContent(cloudFile);
	        		baos = new ByteArrayOutputStream(4046);
	        		IOUtils.copy(is, baos);
	        		String base64 = WBBase64Utility.toBase64(baos.toByteArray());
	        		String htmlImage = String.format("data:%s;base64,%s", image.getAdjustedContentType(), base64);
	        		env.getOut().write(htmlImage);
        		} catch (IOException e)
        		{
        			log.log(Level.SEVERE, "Error when generating base64 image for " + externalKey);
        			throw e;
        		}
        		finally 
        		{
        			IOUtils.closeQuietly(is);
        			IOUtils.closeQuietly(baos);
        		}
        	}
        } catch (WBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerModuleDirective IO exception when reading image");               	
        }
    }

}
