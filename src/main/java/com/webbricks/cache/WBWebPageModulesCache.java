package com.webbricks.cache;

import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;

public interface WBWebPageModulesCache extends WBRefreshableCache {

	public WBWebPageModule getByExternalKey(String key) throws WBIOException;
	
	public WBWebPageModule get(String pageName) throws WBIOException;

}