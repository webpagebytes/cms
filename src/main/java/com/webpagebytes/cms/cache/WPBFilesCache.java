package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBFilesCache extends WPBRefreshableCache {

	public WPBFile getByExternalKey(String externalKey)throws WPBIOException;
	
}
