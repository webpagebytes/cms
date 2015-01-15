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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.engine.WPBCacheInstances;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

class FreeMarkerImageDirective implements TemplateDirectiveModel {
	private static final Logger log = Logger.getLogger(FreeMarkerModuleDirective.class.getName());
	WPBCacheInstances cacheInstances;
	WPBFileStorage cloudFileStorage;
	
	public FreeMarkerImageDirective()
	{
		
	}
	public void initialize(WPBFileStorage cloudFileStorage, WPBCacheInstances cacheInstances)
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
        	WPBFile file = cacheInstances.getFilesCache().getByExternalKey(externalKey);
        	if (file == null)
        	{
        		log.log(Level.WARNING, "cannot find iamge with key" + externalKey);
        		return;
        	}
        	WPBFilePath cloudFile = new WPBFilePath("public", file.getBlobKey());
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
	        		String base64 = CmsBase64Utility.toBase64(baos.toByteArray());
	        		String htmlImage = String.format("data:%s;base64,%s", file.getAdjustedContentType(), base64);
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
        } catch (WPBIOException e)
        {
        	log.log(Level.SEVERE, "ERROR: ", e);
        	throw new TemplateModelException("WBFreeMarkerModuleDirective IO exception when reading image");               	
        }
    }

}
