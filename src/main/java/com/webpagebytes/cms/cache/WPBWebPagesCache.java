package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBIOException;

public interface WPBWebPagesCache extends WPBRefreshableCache {

	public WBWebPage getByExternalKey(String key) throws WBIOException;
	
	public WBWebPage get(String pageName) throws WBIOException;
	
}
