package com.webpagebytes.cms.local;

import com.webpagebytes.cms.exception.WPBIOException;

public class WPBMemCacheMessagesCache extends WPBLocalMessagesCache {

	public void Refresh() throws WPBIOException
	{
		super.Refresh();
		//put the fingerprint in memcache
	}

}
