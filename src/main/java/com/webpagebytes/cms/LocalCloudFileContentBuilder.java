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

package com.webpagebytes.cms;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.appinterfaces.WPBCloudFileStorage;
import com.webpagebytes.cms.cmsdata.WPBCloudFile;
import com.webpagebytes.cms.cmsdata.WPBCloudFileInfo;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

class LocalCloudFileContentBuilder {

	public static final String LOCAL_FILE_SERVE_URL = "/__wblocalfile/";
	
	private WPBCloudFileStorage cloudFileStorage;
	public LocalCloudFileContentBuilder()
	{
		cloudFileStorage = WPBCloudFileStorageFactory.getInstance();
	}
	public void serveFile(HttpServletRequest request, HttpServletResponse response, String uri) throws WPBIOException
	{
		if (! uri.startsWith(LOCAL_FILE_SERVE_URL))
		{
			return;
		}
		String fullFilePath = uri.substring(LOCAL_FILE_SERVE_URL.length());
		int pos = fullFilePath.indexOf('/');
		String bucket = fullFilePath.substring(0, pos);
		String file = fullFilePath.substring(pos+1);
		file = new String(CmsBase64Utility.fromSafePathBase64(file), Charset.forName("UTF-8"));
		WPBCloudFile cloudFile = new WPBCloudFile(bucket, file);
		InputStream is = null;
		try
		{
			is = cloudFileStorage.getFileContent(cloudFile);
			IOUtils.copy(is, response.getOutputStream());
			WPBCloudFileInfo fileInfo = cloudFileStorage.getFileInfo(cloudFile);
			response.setContentType(fileInfo.getContentType());
			
			// do not close the response outputstream here
		} catch (Exception e)
		{
			throw new WPBIOException("cannot serve file", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}	
	}
}
