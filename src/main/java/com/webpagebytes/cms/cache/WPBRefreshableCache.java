package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.exception.WBIOException;

public interface WPBRefreshableCache {
	public void Refresh() throws WBIOException;

}
