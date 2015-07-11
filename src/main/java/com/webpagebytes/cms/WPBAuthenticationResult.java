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

/**
 * Interface that represents the authentication result of checkAuthentication call from WPBAuthentication interface.
 */
public interface WPBAuthenticationResult {
	/**
	 * Returns the string representation of the authenticated user. For example his email address.
	 * This value will be displayed in the CMS administration web interface as the authenticated user.
	 * @return Authenticated user identifier or null/empty string for requests that are not authenticated.
	 */
	public String getUserIdentifier();
	
	/**
	 * Returns the logout link that will be displayed in the CMS administration web interface.
	 * @return Logout link url
	 */
	public String getLogoutLink();

	/**
	 * Returns the login page link that will be displayed in the CMS administration web interface.
	 * @return Login page link url
	 */
	public String getLoginLink();
	
	/**
	 * For authenticated users returns the profile link that will be displayed in the CMS administration web interface.
	 * @return Profile link url for authenticated users or null for not authenticated requests
	 */
	public String getUserProfileLink();
	
}
