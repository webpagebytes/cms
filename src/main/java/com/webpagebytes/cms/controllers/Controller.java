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

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.engine.JSONToFromObjectConverter;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.engine.WPBFileStorageFactory;
import com.webpagebytes.cms.engine.WPBInternalAdminDataStorage;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class Controller {
	public static final String SORT_PARAMETER_DIRECTION = "sort_dir";
	public static final String SORT_PARAMETER_PROPERTY = "sort_field";
	public static final String SORT_PARAMETER_DIRECTION_ASC = "asc";
	public static final String SORT_PARAMETER_DIRECTION_DSC = "dsc";
	public static final String PAGINATION_START = "index_start";
	public static final String PAGINATION_COUNT = "count";
	public static final String PAGINATION_TOTAL_COUNT = "total_count";
	public static final String DATA = "data";
	public static final String ADDTIONAL_DATA = "additional_data";
	public<T> List<T> filterPagination(HttpServletRequest request, List<T> records, Map<String, Object> additionalInfo)
	{
		String paginationStart = request.getParameter(PAGINATION_START);
		String paginationCount = request.getParameter(PAGINATION_COUNT);
		ArrayList<T> result = new ArrayList<T>();
		
		if (paginationCount == null || paginationStart == null)
		{
			additionalInfo.put(PAGINATION_START, 0);
			additionalInfo.put(PAGINATION_COUNT, records.size());
			additionalInfo.put(PAGINATION_TOTAL_COUNT, records.size());
			result.addAll(records);
			return result;
		} 

		int start = 0;
		int count = 0;
		
		try {
			start = Integer.valueOf(paginationStart);
			count = Integer.valueOf(paginationCount);
		} catch (NumberFormatException e)
		{
			additionalInfo.put(PAGINATION_START, 0);
			additionalInfo.put(PAGINATION_COUNT, records.size());
			additionalInfo.put(PAGINATION_TOTAL_COUNT, records.size());
			result.addAll(records);
			return result;
		}
		
		if (count < 0 || start < 0)
		{
			result.addAll(records);
			return result;			
		}
		
		int end = start + count;
		
		for(int i = start; i < end && i < records.size(); i++)
		{
			result.add(records.get(i));
		}
		
		additionalInfo.put(PAGINATION_START, start);
		additionalInfo.put(PAGINATION_COUNT, result.size());
		additionalInfo.put(PAGINATION_TOTAL_COUNT, records.size());

		return result;
	}

	public org.json.JSONArray filterPagination(HttpServletRequest request, org.json.JSONArray records, Map<String, Object> additionalInfo)
	{
		String paginationStart = request.getParameter(PAGINATION_START);
		String paginationCount = request.getParameter(PAGINATION_COUNT);
		
		if (paginationCount == null || paginationStart == null)
		{
			additionalInfo.put(PAGINATION_START, 0);
			additionalInfo.put(PAGINATION_COUNT, records.length());
			additionalInfo.put(PAGINATION_TOTAL_COUNT, records.length());
			return records;
		} 

		int start = 0;
		int count = 0;
		
		try {
			start = Integer.valueOf(paginationStart);
			count = Integer.valueOf(paginationCount);
		} catch (NumberFormatException e)
		{
			additionalInfo.put(PAGINATION_START, 0);
			additionalInfo.put(PAGINATION_COUNT, records.length());
			additionalInfo.put(PAGINATION_TOTAL_COUNT, records.length());
			return records;
		}
		
		if (count < 0 || start < 0)
		{
			return records;			
		}
		
		int end = start + count;
		org.json.JSONArray result = new org.json.JSONArray();
		
		for(int i = start; i < end && i < records.length(); i++)
		{
			try 
			{
				result.put(records.get(i));
			} catch (JSONException e)
			{
				return null;
			}
		}
		
		additionalInfo.put(PAGINATION_START, start);
		additionalInfo.put(PAGINATION_COUNT, result.length());
		additionalInfo.put(PAGINATION_TOTAL_COUNT, records.length());

		return result;
	}

	
	private String adminUriPart;
	protected WPBInternalAdminDataStorage adminStorage;
	protected WPBFileStorage cloudFileStorage;
    
	protected HttpServletToolbox httpServletToolbox;
	protected JSONToFromObjectConverter jsonObjectConverter;

	public String getAdminUriPart() {
		return adminUriPart;
	}

	public void setAdminUriPart(String adminUriPart) {
		this.adminUriPart = adminUriPart;
	}
	
	public Controller() {
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new JSONToFromObjectConverter();
		cloudFileStorage = WPBFileStorageFactory.getInstance();
		adminStorage = WPBAdminDataStorageFactory.getInstance();
	}
	
   protected void deleteFile(WPBFile file) throws IOException
    {
        if (file.getBlobKey() != null)
        {
            WPBFilePath cloudFile = new WPBFilePath(FileController.PUBLIC_BUCKET, file.getBlobKey());
            if (file.getBlobKey() != null && file.getBlobKey().length()>0)
            {
                // make sure the file is deleted only if there is a blob key
                cloudFileStorage.deleteFile(cloudFile);
            }
        }
    }
    protected void deleteAll() throws WPBException, IOException
    {

        adminStorage.deleteAllRecords(WPBUri.class);
        adminStorage.deleteAllRecords(WPBPage.class);
        adminStorage.deleteAllRecords(WPBPageModule.class);
        adminStorage.deleteAllRecords(WPBArticle.class);
        adminStorage.deleteAllRecords(WPBMessage.class);
        adminStorage.deleteAllRecords(WPBParameter.class);
        adminStorage.deleteAllRecords(WPBProject.class);
        List<WPBFile> files = adminStorage.getAllRecords(WPBFile.class);
        for(WPBFile file: files)
        {
            deleteFile(file);
        }
        adminStorage.deleteAllRecords(WPBFile.class);
        adminStorage.deleteAllRecords(WPBResource.class);
   
    }

}
