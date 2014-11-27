package com.webpagebytes.cms.cache;

import java.util.Set;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBUrisCache extends WPBRefreshableCache {

	public static final int HTTP_GET_INDEX = 0;
	public static final int HTTP_POST_INDEX = 1;
	public static final int HTTP_PUT_INDEX = 2;
	public static final int HTTP_DELETE_INDEX = 3;
	
	public WBUri getByExternalKey(String key) throws WPBIOException;
	
	public WBUri get(String uri, int httpIndex) throws WPBIOException;

	public Set<String> getAllUris(int httpIndex) throws WPBIOException;	
	
	public Long getCacheFingerPrint();
	
	public int httpToOperationIndex(String httpOperation);
	public String indexOperationToHttpVerb(int index);
	

}
