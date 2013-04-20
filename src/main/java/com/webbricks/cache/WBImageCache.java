package com.webbricks.cache;

import com.webbricks.cmsdata.WBImage;
import com.webbricks.exception.WBIOException;

public interface WBImageCache extends WBRefreshableCache {

	public WBImage get(Long externalKey)throws WBIOException;
	
}
