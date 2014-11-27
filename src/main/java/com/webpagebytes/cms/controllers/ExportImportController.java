package com.webpagebytes.cms.controllers;

import java.io.IOException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.exception.WPBException;

public class ExportImportController extends Controller {
	private FlatStorageImporterExporter storageExporter;
	
	public ExportImportController()
	{
		storageExporter = new FlatStorageImporterExporter();
	}

	public void importContent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			  ServletFileUpload upload = new ServletFileUpload();
		      
		      FileItemIterator iterator = upload.getItemIterator(request);
		      while (iterator.hasNext()) {
		        FileItemStream item = iterator.next(); 
		        if (!item.isFormField() && item.getFieldName().equals("file")) {
		          InputStream is = item.openStream();
		          storageExporter.importFromZip(is);		  		
		          
		          WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		  		  wbCacheFactory.createWBUrisCacheInstance().Refresh();
		  		  wbCacheFactory.createWBWebPagesCacheInstance().Refresh();
		  		  wbCacheFactory.createWBWebPageModulesCacheInstance().Refresh();
		  		  wbCacheFactory.createWBArticlesCacheInstance().Refresh();
		  		  wbCacheFactory.createWBMessagesCacheInstance().Refresh();
		  		  wbCacheFactory.createWBFilesCacheInstance().Refresh();
		  		  wbCacheFactory.createWBParametersCacheInstance().Refresh();
		  		  wbCacheFactory.createWBProjectCacheInstance().Refresh();
		  		  
		          org.json.JSONObject returnJson = new org.json.JSONObject();
		          returnJson.put(DATA, "");			
		          httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
		        }
		      }		
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANNOT_IMPORT_PROJECT);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void exportContent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		
		try
		{
			response.setContentType("application/zip");
			
			storageExporter.exportToZip(response.getOutputStream());
			
		} catch (IOException e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANNOT_EXPORT_PROJECT);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
				
}
