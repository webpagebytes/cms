package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBFilesCache extends WPBRefreshableCache {

	public WBFile getByExternalKey(String externalKey)throws WPBIOException;
	
}
