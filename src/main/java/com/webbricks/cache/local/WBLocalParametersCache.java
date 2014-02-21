package com.webbricks.cache.local;

import java.util.List;

import com.webbricks.cache.WBParametersCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.exception.WBIOException;

public class WBLocalParametersCache implements WBParametersCache {
	public WBParameter getByExternalKey(String externalKey) throws WBIOException
	{
		return null;
	}
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WBIOException
	{
		return null;
	}

	@Override
	public void Refresh() throws WBIOException {
		// TODO Auto-generated method stub
		
	}
}
