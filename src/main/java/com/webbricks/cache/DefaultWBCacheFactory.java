package com.webbricks.cache;

import com.webbricks.cache.local.WBLocalCacheFactory;

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
