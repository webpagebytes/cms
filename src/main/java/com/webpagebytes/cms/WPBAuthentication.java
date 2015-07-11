/*
 *   Copyright 2015 Webpagebytes
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

/**
 * Interface that authenticates requests to the administration APIs of Webpagebytes CMS.
 * The interface completely abstracts how the authentication is implemented, can be either an application specific implementation
 * or it can use a 3rd party authentication solution (i.e. Google, Yahoo, Facebook,...).
 */
public interface WPBAuthentication {
	public static final String CONFIG_LOGIN_REDIRECT = "loginRedirect";
	public static final String CONFIG_LOGIN_PAGE_URL = "loginPageUrl";
	public static final String CONFIG_LOGOUT_URL = "logoutUrl";	
	public static final String CONFIG_PROFILE_URL = "profileUrl";	
	public static final String CONFIG_USER_IDENTIFIER = "userIdentifier";
	
	/**
	 * Initializes the authentication with parameters from the CMS configuration xml file that corresponds to wpbauthentication section.
	 * @param params Map of keys and values representing configuration parameters
	 * @throws WPBIOException Exception
	 */
	public void initialize(Map<String, String> params) throws WPBException;
	
	/**
	 * Method that checks if a request is authenticated. A typical implementation will use a HTTP session cookie to track and
	 * validate the request(s).
	 * @param request The HTTP request that needs to be authenticated
	 * @return A WPBAuthenticationResult instance. If the userIdentifier member of WPBAuthenticationResult instance that is returned is 
	 * 		   not set then the request will be interpreted by the CMS engine as not authenticated, in this case the web browser will be 
	 * 		   redirected to the loginLink member value of the same instance. <br>
	 *         If the return value is null then the CMS engine will treat the request as authenticated.
	 *         The CMS administration interface will display the authenticated user as the value of the userIdentifier member of the returned instance.
	 * @throws WPBIOException Exception
	 */
	public WPBAuthenticationResult checkAuthentication(HttpServletRequest request) throws WPBIOException;
}
