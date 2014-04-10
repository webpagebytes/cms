package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.exception.WBIOException;

public interface WBRefreshableCache {
	public void Refresh() throws WBIOException;

}
