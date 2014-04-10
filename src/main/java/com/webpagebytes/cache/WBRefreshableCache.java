package com.webpagebytes.cache;

import com.webpagebytes.exception.WBIOException;

public interface WBRefreshableCache {
	public void Refresh() throws WBIOException;

}
