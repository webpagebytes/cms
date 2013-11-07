package com.webbricks.cache;

import java.util.Set;

import com.webbricks.cms.Pair;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.exception.WBIOException;

public interface WBProjectCache extends WBRefreshableCache {
	public String getDefaultLanguage() throws WBIOException;
	public Pair<String, String> getDefaultLocale() throws WBIOException;
	public Set<String> getSupportedLanguages() throws WBIOException;	
	public WBProject getProject() throws WBIOException;
}
