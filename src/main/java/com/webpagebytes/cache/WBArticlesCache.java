package com.webpagebytes.cache;

import com.webpagebytes.cmsdata.WBArticle;
import com.webpagebytes.exception.WBIOException;

public interface WBArticlesCache extends WBRefreshableCache {
	public WBArticle getByExternalKey(String externalKey) throws WBIOException;
	
}
