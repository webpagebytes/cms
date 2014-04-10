package com.webpagebytes.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.webpagebytes.datautility.FlatStorageImporterExporter;
import com.webpagebytes.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.exception.WBException;
import com.webpagebytes.utility.HttpServletToolbox;

public class WBExportImportController extends WBController {
	private HttpServletToolbox httpServletToolbox;
	private FlatStorageImporterExporter storageExporter;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	
	public WBExportImportController()
	{
		httpServletToolbox = new HttpServletToolbox();
		storageExporter = new FlatStorageImporterExporter();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		
	}

	public void importContent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
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
		          org.json.JSONObject returnJson = new org.json.JSONObject();
		          returnJson.put(DATA, "");			
		          httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
		        }
		      }		
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANNOT_IMPORT_PROJECT);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void exportContent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		
		try
		{
			response.setContentType("application/zip");
			
			storageExporter.exportToZip(response.getOutputStream());
			
		} catch (IOException e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANNOT_EXPORT_PROJECT);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
				
}
