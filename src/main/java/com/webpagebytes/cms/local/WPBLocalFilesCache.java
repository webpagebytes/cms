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

package com.webpagebytes.cms.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalFilesCache implements WPBFilesCache {
	
    private static final int MAX_DIR_DEPTH = 25;
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBFile> localCache;
	
	private Map<String, WPBFile> pathToFiles;
	private Map<WPBFile, String> filesToPath;
    
	private static final Object lock = new Object();
	public WPBLocalFilesCache()
	{
		dataStorage = WPBAdminDataStorageFactory.getInstance();
		try
		{
			if (dataStorage != null)
			{
				Refresh();
			}
		} catch (WPBIOException e)
		{
			
		}
	}
	public WPBFile getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCache == null)
		{
			Refresh();
		}
		if (localCache != null)
		{
			return localCache.get(externalKey);
		}
		return null;
	}

	private void RefreshDirectoryTree(WPBFile directory, Map<WPBFile, String> filesToPath, int level) throws WPBIOException
	{
	    if (level > MAX_DIR_DEPTH)
	    {
	        // to prevent infinite loop
	        return;
	    }
	    String ownerExtKey = "";
	    if (null != directory)
	    {
	        ownerExtKey = directory.getExternalKey();
	    }
	    
        List<WPBFile> fileThisDir = dataStorage.query(WPBFile.class, "ownerExtKey", AdminQueryOperator.EQUAL, ownerExtKey);
	    
	    String currentDirPath = filesToPath.get(directory); 
	    for(WPBFile file: fileThisDir)
	    {
	        String path = file.getFileName(); 
	        if (currentDirPath.length() > 0)
	        {
	                path = currentDirPath + "/" + file.getFileName();
	        }
	        filesToPath.put(file, path);
	        if (file.getDirectoryFlag() != null && file.getDirectoryFlag() == 1)
	        {
	            RefreshDirectoryTree(file, filesToPath, level+1);
	        }
	    }
	}
	
	public String getFullFilePath(WPBFile file) throws WPBIOException
	{
	    if (null == file)
	    {
	        return null;
	    }
	    String result = "";
	    while (file != null)
	    {     
	        result = file.getFileName() + "/" + result;
	        file = getByExternalKey(file.getOwnerExtKey());
	    }
        return result;
	}
    
	public WPBFile geByPath(String filePath) throws WPBIOException
	{
	    if (filePath == null) return null;
	    if (filePath.startsWith("/")) filePath = filePath.substring(1);
	    if (filePath.endsWith("/")) filePath = filePath.substring(0, filePath.length()-1);
	    if (null == pathToFiles)
	    {
	        Refresh();
	    }
	    if (pathToFiles != null)
	    {
	        return pathToFiles.get(filePath);
	    }
	    return null;
	}
	
	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBFile> tempMap = new HashMap<String, WPBFile>();
			List<WPBFile> recList = dataStorage.getAllRecords(WPBFile.class);
			for(WPBFile item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			Map<WPBFile, String> filesToPath = new HashMap<WPBFile, String>();
			filesToPath.put(null, "");
			RefreshDirectoryTree(null, filesToPath, 0);
			
			Map<String, WPBFile> pathToFiles = new HashMap<String, WPBFile>();
			for(WPBFile file: filesToPath.keySet())
			{
			    pathToFiles.put(filesToPath.get(file), file);
			}
			this.filesToPath = filesToPath;
			this.pathToFiles = pathToFiles;
			
			localCache = tempMap;
		}
		
	}
	
}
