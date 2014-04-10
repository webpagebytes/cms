package com.webpagebytes.cms.cache;

import java.util.List;

import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.exception.WBIOException;

public interface WBParametersCache extends WBRefreshableCache {

	public WBParameter getByExternalKey(String externalKey) throws WBIOException;
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WBIOException;

}
