package com.webpagebytes.cms;

import java.io.IOException;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.webpagebytes.cache.WBCacheInstances;
import com.webpagebytes.cache.WBFilesCache;
import com.webpagebytes.cmsdata.WBFile;
import com.webpagebytes.datautility.WBCloudFile;
import com.webpagebytes.datautility.WBCloudFileStorage;
import com.webpagebytes.datautility.WBCloudFileStorageFactory;
import com.webpagebytes.exception.WBException;
import com.webpagebytes.exception.WBIOException;

public class FileContentBuilder {
	private WBCloudFileStorage cloudFileStorage;
	private WBFilesCache filesCache;
	public FileContentBuilder(WBCacheInstances cacheInstances)
	{
		filesCache = cacheInstances.getWBFilesCache();
		cloudFileStorage = WBCloudFileStorageFactory.getInstance();
	}
	public void initialize()
	{
	}
	
	public WBFile find(String externalKey) throws WBException
	{
		return filesCache.getByExternalKey(externalKey);
	}
	public InputStream getFileContent(WBFile file) throws WBException
	{
		WBCloudFile cloudFile = new WBCloudFile("public", file.getBlobKey());
		try
		{
			return cloudFileStorage.getFileContent(cloudFile);
		} catch (IOException e)
		{
			throw new WBException ("cannot get file content ", e);
		}
	}
	public void writeFileContent(WBFile wbFile, OutputStream os) throws WBException 
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
			throw new WBIOException(e.getMessage(), e);
		}
	}
}
