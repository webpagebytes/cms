package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.exception.WBIOException;

public interface WBFilesCache extends WBRefreshableCache {

	public WBFile getByExternalKey(String externalKey)throws WBIOException;
	
}
