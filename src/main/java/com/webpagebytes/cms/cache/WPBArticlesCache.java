package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBArticlesCache extends WPBRefreshableCache {
	public WPBArticle getByExternalKey(String externalKey) throws WPBIOException;
	
}
