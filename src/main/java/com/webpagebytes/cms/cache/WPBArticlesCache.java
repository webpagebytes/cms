package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBArticlesCache extends WPBRefreshableCache {
	public WBArticle getByExternalKey(String externalKey) throws WPBIOException;
	
}
