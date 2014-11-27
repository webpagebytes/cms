package com.webpagebytes.cms;

import java.io.IOException;



import java.io.InputStream;
import java.io.OutputStream;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBFilesCache;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.datautility.WPBCloudFile;
import com.webpagebytes.cms.datautility.WPBCloudFileStorage;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

class FileContentBuilder {
	private WPBCloudFileStorage cloudFileStorage;
	private WPBFilesCache filesCache;
	public FileContentBuilder(WPBCacheInstances cacheInstances)
	{
		filesCache = cacheInstances.getWBFilesCache();
		cloudFileStorage = WPBCloudFileStorageFactory.getInstance();
	}
	public void initialize()
	{
	}
	
	public WBFile find(String externalKey) throws WPBException
	{
		return filesCache.getByExternalKey(externalKey);
	}
	public InputStream getFileContent(WBFile file) throws WPBException
	{
		WPBCloudFile cloudFile = new WPBCloudFile("public", file.getBlobKey());
		try
		{
			return cloudFileStorage.getFileContent(cloudFile);
		} catch (IOException e)
		{
			throw new WPBException ("cannot get file content ", e);
		}
	}
	public void writeFileContent(WBFile wbFile, OutputStream os) throws WPBException 
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
