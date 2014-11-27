package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBWebPageModulesCache extends WPBRefreshableCache {

	public WBWebPageModule getByExternalKey(String key) throws WPBIOException;
	
	public WBWebPageModule get(String pageName) throws WPBIOException;

}