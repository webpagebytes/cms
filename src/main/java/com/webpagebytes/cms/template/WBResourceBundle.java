package com.webpagebytes.cms.template;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.webpagebytes.cms.cache.WBMessagesCache;
import com.webpagebytes.cms.exception.WBIOException;

public class WBResourceBundle extends ResourceBundle {

	private WBMessagesCache messageCache;
	private Long fingerPrint;
	private Map<String, String> messages;
	
	WBResourceBundle(WBMessagesCache messageCache, Locale locale)
	{
		this.messageCache = messageCache;
		fingerPrint = 0L;
		messages = new HashMap<String, String>();
		Refresh(locale);
	}
	
	public void Refresh(Locale locale)
	{
		try {
			Long aFingerPrint = messageCache.getFingerPrint(locale); 
			if (aFingerPrint.equals(0L) || !aFingerPrint.equals(fingerPrint))
			{
				messages = messageCache.getAllMessages(locale);
				aFingerPrint = messageCache.getFingerPrint(locale); 
				fingerPrint = aFingerPrint;
			}
		} catch (WBIOException e)
		{
			fingerPrint = 0L;
		}
	}
	
	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(messages.keySet());
	}

	@Override
	protected Object handleGetObject(String arg0) {
		return messages.get(arg0);
	}
	
	
	public Long getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(Long fingerPrint) {
		this.fingerPrint = fingerPrint;
	}


}
