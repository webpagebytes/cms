package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.exception.WBIOException;

public interface WBArticlesCache extends WBRefreshableCache {
	public WBArticle getByExternalKey(String externalKey) throws WBIOException;
	
}
