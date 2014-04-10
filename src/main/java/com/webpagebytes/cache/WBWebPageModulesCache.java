package com.webpagebytes.cache;

import com.webpagebytes.cmsdata.WBWebPageModule;
import com.webpagebytes.exception.WBIOException;

public interface WBWebPageModulesCache extends WBRefreshableCache {

	public WBWebPageModule getByExternalKey(String key) throws WBIOException;
	
	public WBWebPageModule get(String pageName) throws WBIOException;

}