package com.webbricks.cache;

import java.util.Set;

import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.exception.WBIOException;

public interface WBWebPagesCache extends WBRefreshableCache {

	public WBWebPage getByExternalKey(String key) throws WBIOException;
	
	public WBWebPage get(String pageName) throws WBIOException;
	
}
