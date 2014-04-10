package com.webpagebytes.cache;

import java.util.List;

import com.webpagebytes.cmsdata.WBParameter;
import com.webpagebytes.exception.WBIOException;

public interface WBParametersCache extends WBRefreshableCache {

	public WBParameter getByExternalKey(String externalKey) throws WBIOException;
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WBIOException;

}
