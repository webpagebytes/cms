package com.webbricks.cache;

import com.webbricks.cmsdata.WBFile;
import com.webbricks.exception.WBIOException;

public interface WBFilesCache extends WBRefreshableCache {

	public WBFile get(Long externalKey)throws WBIOException;
	
}
