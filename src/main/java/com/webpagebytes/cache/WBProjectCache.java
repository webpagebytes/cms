package com.webpagebytes.cache;

import java.util.Set;

import com.webpagebytes.cms.Pair;
import com.webpagebytes.cmsdata.WBProject;
import com.webpagebytes.exception.WBIOException;

public interface WBProjectCache extends WBRefreshableCache {
	public String getDefaultLanguage() throws WBIOException;
	public Pair<String, String> getDefaultLocale() throws WBIOException;
	public Set<String> getSupportedLanguages() throws WBIOException;	
	public WBProject getProject() throws WBIOException;
}
