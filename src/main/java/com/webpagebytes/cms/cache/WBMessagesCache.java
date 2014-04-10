package com.webpagebytes.cms.cache;

import java.util.Locale;
import java.util.Map;

import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.exception.WBIOException;

public interface WBMessagesCache extends WBRefreshableCache {
	public Map<String, String> getAllMessages(Locale locale) throws WBIOException;
	public Map<String, String> getAllMessages(String lcid) throws WBIOException;
	public Long getFingerPrint(Locale locale);
}
