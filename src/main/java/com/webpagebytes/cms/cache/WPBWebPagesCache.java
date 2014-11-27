package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBWebPagesCache extends WPBRefreshableCache {

	public WBWebPage getByExternalKey(String key) throws WPBIOException;
	
	public WBWebPage get(String pageName) throws WPBIOException;
	
}
