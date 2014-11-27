package com.webpagebytes.cms.cache;

import java.util.List;

import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBParametersCache extends WPBRefreshableCache {

	public WBParameter getByExternalKey(String externalKey) throws WPBIOException;
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WPBIOException;

}
