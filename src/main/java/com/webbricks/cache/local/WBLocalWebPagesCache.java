package com.webbricks.cache.local;
import com.webbricks.cache.WBWebPagesCache;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.exception.WBIOException;

public class WBLocalWebPagesCache implements WBWebPagesCache {
	public WBWebPage getByExternalKey(String key) throws WBIOException
	{
		return null;
	}
	
	public WBWebPage get(String pageName) throws WBIOException
	{
		return null;
	}

	@Override
	public void Refresh() throws WBIOException {
		// TODO Auto-generated method stub
		
	}
}
