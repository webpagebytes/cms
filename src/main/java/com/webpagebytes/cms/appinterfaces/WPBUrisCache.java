/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.appinterfaces;

import java.util.Set;

import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBUrisCache extends WPBRefreshableCache {

	public static final int HTTP_GET_INDEX = 0;
	public static final int HTTP_POST_INDEX = 1;
	public static final int HTTP_PUT_INDEX = 2;
	public static final int HTTP_DELETE_INDEX = 3;
	
	public WPBUri getByExternalKey(String key) throws WPBIOException;
	
	public WPBUri get(String uri, int httpIndex) throws WPBIOException;

	public Set<String> getAllUris(int httpIndex) throws WPBIOException;	
	
	public Long getCacheFingerPrint();
	
	public int httpToOperationIndex(String httpOperation);
	public String indexOperationToHttpVerb(int index);
	

}
