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

/**
 * Interface used to forward the request from a WPBRequestHandler controller to a particular site page.
 *
 */
public class WPBForward {
	String redirectToExternalKey = null;
	boolean forwardRequest = false;
	
	/**
	 * Specify the site page externalKey that the request will be forwarded.
	 * @param externalKey The site page externalKey that will provide the content of the current HTTP request.
	 */
	public void setForwardTo(String externalKey)
	{
		redirectToExternalKey = externalKey;
		forwardRequest = false;
		if (externalKey != null && externalKey.length()>0)
		{
			forwardRequest = true;
		}
	}
	
	/**
	 * Returns the site page externalKey that was set to forward the request, or null otherwise.
	 * @return Returns the site page externalKey that was set to forward the request, or null otherwise.
	 */
	public String getForwardTo()
	{
		return redirectToExternalKey;
	}
	
	/**
	 * Returns a boolean value that specifies if the current request was forwarded or not.
	 * @return Returns a boolean value that specifies if the current request was forwarded or not.
	 */
	public boolean isRequestForwarded()
	{
		return forwardRequest;
	}
	
}
