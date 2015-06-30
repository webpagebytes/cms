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
