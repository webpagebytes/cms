package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBWebPagesCache extends WPBRefreshableCache {

	public WPBWebPage getByExternalKey(String key) throws WPBIOException;
	
	public WPBWebPage get(String pageName) throws WPBIOException;
	
}
