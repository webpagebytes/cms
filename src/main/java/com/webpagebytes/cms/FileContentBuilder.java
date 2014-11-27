package com.webpagebytes.cms;

import java.io.IOException;



import java.io.InputStream;
import java.io.OutputStream;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBFilesCache;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.datautility.WPBCloudFile;
import com.webpagebytes.cms.datautility.WPBCloudFileStorage;
import com.webpagebytes.cms.datautility.WBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;

class FileContentBuilder {
	private WPBCloudFileStorage cloudFileStorage;
	private WPBFilesCache filesCache;
	public FileContentBuilder(WPBCacheInstances cacheInstances)
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
		WPBCloudFile cloudFile = new WPBCloudFile("public", file.getBlobKey());
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
