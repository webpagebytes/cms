package com.webpagebytes.cms.cache;

import java.util.Set;

import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBIOException;

public interface WBWebPagesCache extends WBRefreshableCache {

	public WBWebPage getByExternalKey(String key) throws WBIOException;
	
	public WBWebPage get(String pageName) throws WBIOException;
	
}
