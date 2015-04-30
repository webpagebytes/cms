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

package com.webpagebytes.cms;

import java.util.Set;

import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.exception.WPBIOException;

/**
 * Cache abstract class to access CMS site url records.
 */
public abstract class WPBUrisCache implements WPBRefreshableCache {

    /**
     * Numeric identifier for GET verb
     */
	public static final int HTTP_GET_INDEX = 0;
	
	/**
	 * Numeric identifier for POST verb
	 */
	public static final int HTTP_POST_INDEX = 1;
	
	/**
	 * Numeric identifier for PUT verb
	 */
	public static final int HTTP_PUT_INDEX = 2;
	
	/**
	 * Numeric identifier for DELETE verb
	 */
	public static final int HTTP_DELETE_INDEX = 3;

	/**
     * Numeric identifier for HEAD verb
     */
    public static final int HTTP_HEAD_INDEX = 4;

    /**
     * Numeric identifier for OPTIONS verb
     */
    public static final int HTTP_OPTIONS_INDEX = 5;

    /**
     * Numeric identifier for PATCH verb
     */
    public static final int HTTP_PATCH_INDEX = 6;

	
	/**
	 * Gets a WPBUri from cache based on its externalKey
	 * @param externalKey externalKey that identifies the record. 
	 * @return WPBUri instance or null if there is no record with the provided externalKey. 
	 * @throws WPBIOException Exception 
	 */
	public abstract WPBUri getByExternalKey(String externalKey) throws WPBIOException;
	
	/**
	 * Gets a WPBUri from cache based on uri path and HTTP verb index
	 * @param uri Site uri path
	 * @param httpIndex HTTP verb index
	 * @return WPBUri instance or null if there is no record with the provided externalKey.
	 * @throws WPBIOException Exception
	 */
	public abstract WPBUri get(String uri, int httpIndex) throws WPBIOException;

	/**
	 * Returns all site url paths for a HTTP verb index
	 * @param httpIndex HTTP verb index
	 * @return A set of all sire urls paths for a HTTP verb index
	 * @throws WPBIOException Exception
	 */
	public abstract Set<String> getAllUris(int httpIndex) throws WPBIOException;	
		
	/**
	 * Utility method to convert a HTTP verb to the corresponsing index value
	 * @param httpOperation HTTP verb operation
	 * @return Returns the correspondent HTTP_XXX_INDEX value or -1 if the parameter provided is not supported
	 */
    public int httpToOperationIndex(String httpOperation)
    {
        if (httpOperation.toUpperCase().equals("GET"))
        {
            return HTTP_GET_INDEX;
        } else if (httpOperation.toUpperCase().equals("POST"))
        {
            return HTTP_POST_INDEX;
        } else if (httpOperation.toUpperCase().equals("PUT"))
        {
            return HTTP_PUT_INDEX;
        } else if (httpOperation.toUpperCase().equals("DELETE"))
        {
            return HTTP_DELETE_INDEX;
        } else if (httpOperation.toUpperCase().equals("HEAD"))
        {
            return HTTP_HEAD_INDEX;
        } else if (httpOperation.toUpperCase().equals("OPTIONS"))
        {
            return HTTP_OPTIONS_INDEX;
        } else if (httpOperation.toUpperCase().equals("PATCH"))
        {
            return HTTP_PATCH_INDEX;
        }
        return -1;  
    }
    
    /**
     * Utility method to convert an HTTP_XXX_INDEX to the corresponding string verb
     * @param httpIndex HTTP_XXX_INDEX value
     * @return Returns the HTTP string verb
     */
    public String indexOperationToHttpVerb(int httpIndex)
    {
        if (httpIndex == WPBUrisCache.HTTP_GET_INDEX)
        {
            return "GET";
        } else if (httpIndex == WPBUrisCache.HTTP_POST_INDEX)
        {
            return "POST";
        } else if (httpIndex == WPBUrisCache.HTTP_PUT_INDEX)
        {
            return "PUT";
        } else if (httpIndex == WPBUrisCache.HTTP_DELETE_INDEX)
        {
            return "DELETE";
        } else if (httpIndex == WPBUrisCache.HTTP_HEAD_INDEX)
        {
            return "HEAD";
        } else if (httpIndex == WPBUrisCache.HTTP_OPTIONS_INDEX)
        {
            return "OPTIONS";
        } else if (httpIndex == WPBUrisCache.HTTP_PATCH_INDEX)
        {
            return "PATCH";
        }
        return null;
    }


}
