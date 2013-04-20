package com.webbricks.cache;

import com.webbricks.cmsdata.WBArticle;
import com.webbricks.exception.WBIOException;

public interface WBArticleCache extends WBRefreshableCache {
	public WBArticle get(Long externalKey) throws WBIOException;
	
}
