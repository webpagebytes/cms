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

package com.webpagebytes.cms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.WPBAuthenticationResult;
import com.webpagebytes.cms.exception.WPBException;

public class ExportImportController extends Controller {
	private FlatStorageImporterExporter storageExporter;
	
	public ExportImportController()
	{
		storageExporter = new FlatStorageImporterExporter();
	}

	public void importContent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}
		
	    File tempFile = null;
		try
		{
			  ServletFileUpload upload = new ServletFileUpload();
		      
		      FileItemIterator iterator = upload.getItemIterator(request);
		      while (iterator.hasNext()) {
		        FileItemStream item = iterator.next(); 
		        if (!item.isFormField() && item.getFieldName().equals("file")) {
		          InputStream is = item.openStream();
		          tempFile = File.createTempFile("wpbImport", null);
		          FileOutputStream fos = new FileOutputStream(tempFile);
		          IOUtils.copy(is, fos);
		          fos.close();
		          
		          adminStorage.stopNotifications();
		          deleteAll();
		          
		          // because the zip file entries cannot be predicted we needto import in two step1
		          // step 1 when we create the resource records
		          // step 2 when we import the files, pages, modules and articles content
		          InputStream is1 = new FileInputStream(tempFile);
		          storageExporter.importFromZipStep1(is1);
		          is1.close();

                  InputStream is2 = new FileInputStream(tempFile);
                  storageExporter.importFromZipStep2(is2);
                  is2.close();

		          returnJson.put(DATA, "");			
		          httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
		        }
		      }		
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANNOT_IMPORT_PROJECT);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
		finally
		{
		    if (tempFile != null)
		    {
		        if (! tempFile.delete())
		        {
		            tempFile.deleteOnExit();
		        }
		    }
		    
		    adminStorage.startNotifications();
		}
	}

	public void exportContent(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
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
