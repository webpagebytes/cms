package com.webpagebytes.cms.local;

import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class WPBMemCacheClient {

	private MemcachedClient client;
	
	public WPBMemCacheClient()
	{
	
	}
	public void initialize(String addresses) throws IOException
	{
		client = new MemcachedClient(AddrUtil.getAddresses(addresses));
	}
	
	public String getFingerPrint(String key)
	{
		Object result = client.get(key);
		if (result != null)
		{
			return result.toString();
		}
		return null;
	}
	public void putFingerPrint(String key, String value)
	{
		client.set(key, 30*5, value);
	}
}
