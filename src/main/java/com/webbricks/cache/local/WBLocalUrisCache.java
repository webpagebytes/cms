package com.webbricks.cache.local;

import java.util.HashSet;
import java.util.Set;

import com.webbricks.cache.WBUrisCache;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.exception.WBIOException;

public class WBLocalUrisCache implements WBUrisCache {

	public WBUri getByExternalKey(String key) throws WBIOException
	{
		return null;
	}
	
	public WBUri get(String uri, int httpIndex) throws WBIOException
	{
		return null;
	}

	public Set<String> getAllUris(int httpIndex) throws WBIOException
	{
		return new HashSet<String>();
	}
	
	public Long getCacheFingerPrint()
	{
		return 0L;
	}
	
	public int httpToOperationIndex(String httpOperation)
	{
		if (httpOperation.toUpperCase().equals("GET"))
		{
			return HTTP_GET_INDEX;
		} else if (httpOperation.toUpperCase().equals("POST"))
		{
			return HTTP_POST_INDEX;
		} else if (httpOperation.toUpperCase().equals("PUT"))
		{
			return HTTP_PUT_INDEX;
		} else if (httpOperation.toUpperCase().equals("DELETE"))
		{
			return HTTP_DELETE_INDEX;
		}
		return -1;	
	}
	public String indexOperationToHttpVerb(int httpIndex)
	{
		if (httpIndex == WBUrisCache.HTTP_GET_INDEX)
		{
			return "GET";
		} else if (httpIndex == WBUrisCache.HTTP_POST_INDEX)
		{
			return "POST";
		} else if (httpIndex == WBUrisCache.HTTP_PUT_INDEX)
		{
			return "PUT";
		} else if (httpIndex == WBUrisCache.HTTP_DELETE_INDEX)
		{
			return "DELETE";
		}
		return null;
	}

	@Override
	public void Refresh() throws WBIOException {
		// TODO Auto-generated method stub
		
	}

}
