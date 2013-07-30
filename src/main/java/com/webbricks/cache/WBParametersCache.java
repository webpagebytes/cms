package com.webbricks.cache;

import java.util.List;

import com.webbricks.cmsdata.WBParameter;
import com.webbricks.exception.WBIOException;

public interface WBParametersCache extends WBRefreshableCache {

	public WBParameter getByExternalKey(String externalKey) throws WBIOException;
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WBIOException;

}
