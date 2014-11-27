package com.webpagebytes.cms.cache;

import java.util.List;

import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBParametersCache extends WPBRefreshableCache {

	public WPBParameter getByExternalKey(String externalKey) throws WPBIOException;
	
	public List<WPBParameter> getAllForOwner(String ownerExternalKey) throws WPBIOException;

}
