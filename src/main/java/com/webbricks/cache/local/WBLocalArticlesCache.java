package com.webbricks.cache.local;

import com.webbricks.cache.WBArticlesCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.exception.WBIOException;

public class WBLocalArticlesCache implements WBArticlesCache {
	public WBArticle getByExternalKey(String externalKey) throws WBIOException
	{
		return null;
	}

	@Override
	public void Refresh() throws WBIOException {
		// TODO Auto-generated method stub
		
	}
}
