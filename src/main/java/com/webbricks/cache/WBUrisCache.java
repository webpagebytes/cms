package com.webbricks.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webbricks.cmsdata.WBUri;
import com.webbricks.exception.WBIOException;

public interface WBUrisCache extends WBRefreshableCache {

	public WBUri getByExternalKey(String key) throws WBIOException;
	
	public WBUri get(String uri) throws WBIOException;

	public Set<String> getAllUris() throws WBIOException;
	
	public Long getCacheFingerPrint();

}
