package com.webpagebytes.cms.local;

import com.webpagebytes.cms.exception.WPBIOException;

public class WPBMemCachePagesCache extends WPBLocalPagesCache {

	public void Refresh() throws WPBIOException
	{
		super.Refresh();
		//put the fingerprint in memcache
	}

}
