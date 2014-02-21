package com.webbricks.cache.local;

import java.util.Locale;
import java.util.Map;

import com.webbricks.cache.WBMessagesCache;
import com.webbricks.exception.WBIOException;

public class WBLocalMessagesCache implements WBMessagesCache {
	public Map<String, String> getAllMessages(Locale locale) throws WBIOException
	{
		return null;
	}
	public Map<String, String> getAllMessages(String lcid) throws WBIOException
	{
		return null;
	}
	public Long getFingerPrint(Locale locale)
	{
		return 0L;
	}
	@Override
	public void Refresh() throws WBIOException {
		// TODO Auto-generated method stub
		
	}
}
