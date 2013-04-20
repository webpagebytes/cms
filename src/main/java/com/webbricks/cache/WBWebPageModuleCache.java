package com.webbricks.cache;

import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;

public interface WBWebPageModuleCache extends WBRefreshableCache {

	public WBWebPageModule get(Long key) throws WBIOException;
	
	public WBWebPageModule get(String pageName) throws WBIOException;

}