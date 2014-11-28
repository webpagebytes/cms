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

public class WPBForward {
	String redirectToExternalKey = null;
	boolean forwardRequest = false;
	
	public void setForwardTo(String externalKey)
	{
		redirectToExternalKey = externalKey;
		forwardRequest = false;
		if (externalKey != null && externalKey.length()>0)
		{
			forwardRequest = true;
		}
	}
	public String getForwardTo()
	{
		return redirectToExternalKey;
	}
	public boolean isRequestForwarded()
	{
		return forwardRequest;
	}
	
}
