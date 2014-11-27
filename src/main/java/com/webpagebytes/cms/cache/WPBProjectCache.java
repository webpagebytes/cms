package com.webpagebytes.cms.cache;

import java.util.Set;

import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.utility.Pair;

public interface WPBProjectCache extends WPBRefreshableCache {
	public String getDefaultLanguage() throws WBIOException;
	public Pair<String, String> getDefaultLocale() throws WBIOException;
	public Set<String> getSupportedLocales() throws WBIOException;	
	public WBProject getProject() throws WBIOException;
}
