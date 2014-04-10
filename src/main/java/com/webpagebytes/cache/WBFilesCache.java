package com.webpagebytes.cache;

import com.webpagebytes.cmsdata.WBFile;
import com.webpagebytes.exception.WBIOException;

public interface WBFilesCache extends WBRefreshableCache {

	public WBFile getByExternalKey(String externalKey)throws WBIOException;
	
}
