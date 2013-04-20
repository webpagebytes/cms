package com.webbricks.cache;

import java.util.Set;

import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.exception.WBIOException;

public interface WBWebPageCache extends WBRefreshableCache {

	public WBWebPage get(Long key) throws WBIOException;
	
	public WBWebPage get(String pageName) throws WBIOException;

}
