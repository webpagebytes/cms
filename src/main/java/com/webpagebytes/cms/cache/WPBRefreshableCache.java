package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBRefreshableCache {
	public void Refresh() throws WPBIOException;

}
