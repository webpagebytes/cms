package com.webpagebytes.cache;

import java.util.Set;

import com.webpagebytes.cmsdata.WBUri;
import com.webpagebytes.cmsdata.WBWebPage;
import com.webpagebytes.exception.WBIOException;

public interface WBWebPagesCache extends WBRefreshableCache {

	public WBWebPage getByExternalKey(String key) throws WBIOException;
	
	public WBWebPage get(String pageName) throws WBIOException;
	
}
