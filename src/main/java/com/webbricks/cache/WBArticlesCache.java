package com.webbricks.cache;

import com.webbricks.cmsdata.WBArticle;
import com.webbricks.exception.WBIOException;

public interface WBArticlesCache extends WBRefreshableCache {
	public WBArticle get(Long externalKey) throws WBIOException;
	
}
