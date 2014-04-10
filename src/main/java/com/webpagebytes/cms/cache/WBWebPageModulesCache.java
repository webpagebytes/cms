package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.exception.WBIOException;

public interface WBWebPageModulesCache extends WBRefreshableCache {

	public WBWebPageModule getByExternalKey(String key) throws WBIOException;
	
	public WBWebPageModule get(String pageName) throws WBIOException;

}