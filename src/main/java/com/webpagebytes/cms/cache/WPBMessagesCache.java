package com.webpagebytes.cms.cache;

import java.util.Locale;

import java.util.Map;
import java.util.Set;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBMessagesCache extends WPBRefreshableCache {
	public Map<String, String> getAllMessages(Locale locale) throws WPBIOException;
	public Map<String, String> getAllMessages(String lcid) throws WPBIOException;
	public Set<String> getSupportedLocales();
	public Long getFingerPrint(Locale locale);
}
