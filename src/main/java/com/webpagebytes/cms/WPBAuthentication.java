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

public interface WPBAuthentication {
	public static final String CONFIG_LOGIN_REDIRECT = "loginRedirect";
	public static final String CONFIG_LOGIN_PAGE_URL = "loginPageUrl";
	public static final String CONFIG_LOGOUT_URL = "logoutUrl";	
	public static final String CONFIG_PROFILE_URL = "profileUrl";	
	public static final String CONFIG_USER_IDENTIFIER = "userIdentifier";
	
	public void initialize(Map<String, String> params) throws WPBException;
	public WPBAuthenticationResult checkAuthentication(HttpServletRequest request) throws WPBIOException;
}
