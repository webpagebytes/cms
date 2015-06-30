package com.webpagebytes.cms;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBLocalAuthentication implements WPBAuthentication {

	public static final String TOKEN_COOKIE = "wpbToken";
	private Map<String, String> authenticationParams = new HashMap<String, String>();
	
	public void initialize(Map<String, String> params) throws WPBException
	{
		if (params != null)
		{
			authenticationParams.putAll(params);
		}		
	}
	
	public static String getTokenCookie(HttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies(); 
		if (cookies == null) return null;
		for(Cookie cookie: cookies)
		{
			if (cookie.getName().equals(TOKEN_COOKIE))
			{
				return cookie.getValue();
			}
		}
		return null;
	}
	
	public WPBAuthenticationResult checkAuthentication(HttpServletRequest request) throws WPBIOException
	{
		// this authentication takes the token as base64, decodes it and verifies if it's a file, if yes then the filename 
		//is the userIdentifier 
		WPBDefaultAuthenticationResult result = new WPBDefaultAuthenticationResult();
		result.setLoginLink(authenticationParams.get(WPBAuthentication.CONFIG_LOGIN_PAGE_URL));
		result.setLogoutLink(authenticationParams.get(WPBAuthentication.CONFIG_LOGOUT_URL));
		result.setUserProfileLink(authenticationParams.get(WPBAuthentication.CONFIG_PROFILE_URL));
		
		String token = getTokenCookie(request);
		if (token == null) return result;
		String path = new String(CmsBase64Utility.fromSafePathBase64(token));
		File file = new File(path);
		if (file.exists())
		{
			result.setUserIdentifier(file.getName());
		}		
		return result;
	}
}
