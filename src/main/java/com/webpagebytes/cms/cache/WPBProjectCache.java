package com.webpagebytes.cms.cache;

import java.util.Set;

import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.Pair;

public interface WPBProjectCache extends WPBRefreshableCache {
	public String getDefaultLanguage() throws WPBIOException;
	public Pair<String, String> getDefaultLocale() throws WPBIOException;
	public Set<String> getSupportedLocales() throws WPBIOException;	
	public WBProject getProject() throws WPBIOException;
}
