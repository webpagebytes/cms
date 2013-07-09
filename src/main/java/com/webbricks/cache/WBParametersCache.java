package com.webbricks.cache;

import java.util.List;

import com.webbricks.cmsdata.WBParameter;
import com.webbricks.exception.WBIOException;

public interface WBParametersCache extends WBRefreshableCache {

	public WBParameter get(Long key) throws WBIOException;
	
	public List<WBParameter> getAllForOwner(Long ownerKey) throws WBIOException;

}
