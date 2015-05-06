package com.webpagebytes.cms.local;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCacheFilesCache implements WPBFilesCache {

	private WPBMemCacheClient memcacheClient;
    private static final String CACHE_KEY = "wpbfilescache";
	private static final int MAX_DIR_DEPTH = 25;
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBFile> localCache;
	private String fingerPrint = "";
	private Map<String, WPBFile> pathToFiles;
	//private Map<WPBFile, String> filesToPath;
    
	private static final Object lock = new Object();
	
	public WPBMemCacheFilesCache(WPBMemCacheClient memcacheClient)
	{
		this.memcacheClient = memcacheClient;
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
			List<WPBFile> recList = dataStorage.getAllRecords(WPBFile.class, "externalKey", AdminSortOperator.ASCENDING);
			
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBFile item: recList)
			{
				md.update(item.getVersion().getBytes());
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
			this.pathToFiles = pathToFiles;
			this.localCache = tempMap;
			fingerPrint = CmsBase64Utility.toBase64(md.digest());
			
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
		}
		
	}
	@Override
	public String getFingerPrint() {
		return fingerPrint;
	}

}
