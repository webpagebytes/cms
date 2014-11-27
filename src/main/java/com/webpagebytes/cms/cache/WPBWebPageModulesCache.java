package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBWebPageModulesCache extends WPBRefreshableCache {

	public WPBWebPageModule getByExternalKey(String key) throws WPBIOException;
	
	public WPBWebPageModule get(String pageName) throws WPBIOException;

}