package com.webbricks.cache;

import java.util.Set;

import com.webbricks.cmsdata.WBProject;
import com.webbricks.exception.WBIOException;

public interface WBProjectCache extends WBRefreshableCache {
	public String getDefaultLanguage() throws WBIOException;
	public Set<String> getSupportedLanguage() throws WBIOException;	
	public WBProject getProject() throws WBIOException;
}
