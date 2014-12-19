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

package com.webpagebytes.cms.engine;

import java.io.IOException;



import java.io.InputStream;
import java.io.OutputStream;

import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

public class FileContentBuilder {
	private WPBFileStorage cloudFileStorage;
	private WPBFilesCache filesCache;
	public FileContentBuilder(WPBCacheInstances cacheInstances)
	{
		filesCache = cacheInstances.getWBFilesCache();
		cloudFileStorage = WPBCloudFileStorageFactory.getInstance();
	}
	public void initialize()
	{
	}
	
	public WPBFile find(String externalKey) throws WPBException
	{
		return filesCache.getByExternalKey(externalKey);
	}
	public InputStream getFileContent(WPBFile file) throws WPBException
	{
		WPBFilePath cloudFile = new WPBFilePath("public", file.getBlobKey());
		try
		{
			return cloudFileStorage.getFileContent(cloudFile);
		} catch (IOException e)
		{
			throw new WPBException ("cannot get file content ", e);
		}
	}
	public void writeFileContent(WPBFile wbFile, OutputStream os) throws WPBException 
	{
		InputStream is = getFileContent(wbFile);
		byte[] buffer = new byte[4096];
		int len;
		try 
		{
			while ((len = is.read(buffer)) != -1) {
			    os.write(buffer, 0, len);
			}
		} catch (IOException e)
		{
			throw new WPBIOException(e.getMessage(), e);
		}
	}
}
