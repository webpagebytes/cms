package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.cache.local.WBLocalCacheFactory;

public class DefaultWBCacheFactory {
	
	private DefaultWBCacheFactory() {};
	private static WBCacheFactory instance = null;
	public static WBCacheFactory getInstance()
	{
		if (instance == null)
		{
			instance = new WBLocalCacheFactory();
		}
		return instance;
	}
}
