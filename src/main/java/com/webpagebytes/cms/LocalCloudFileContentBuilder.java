package com.webpagebytes.cms;

import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.datautility.WPBCloudFile;
import com.webpagebytes.cms.datautility.WPBCloudFileInfo;
import com.webpagebytes.cms.datautility.WPBCloudFileStorage;
import com.webpagebytes.cms.datautility.WBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.utility.WBBase64Utility;

class LocalCloudFileContentBuilder {

	public static final String LOCAL_FILE_SERVE_URL = "/__wblocalfile/";
	
	private WPBCloudFileStorage cloudFileStorage;
	public LocalCloudFileContentBuilder()
	{
		cloudFileStorage = WBCloudFileStorageFactory.getInstance();
	}
	public void serveFile(HttpServletRequest request, HttpServletResponse response, String uri) throws WBIOException
	{
		if (! uri.startsWith(LOCAL_FILE_SERVE_URL))
		{
			return;
		}
		String fullFilePath = uri.substring(LOCAL_FILE_SERVE_URL.length());
		int pos = fullFilePath.indexOf('/');
		String bucket = fullFilePath.substring(0, pos);
		String file = fullFilePath.substring(pos+1);
		file = new String(WBBase64Utility.fromSafePathBase64(file), Charset.forName("UTF-8"));
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
			throw new WBIOException("cannot serve file", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}	
	}
}
