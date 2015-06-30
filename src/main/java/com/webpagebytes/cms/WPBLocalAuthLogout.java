package com.webpagebytes.cms;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBLocalAuthLogout extends HttpServlet {

private String url_login_page = "";
public static final String URL_LOGIN_PAGE = "url-login-page"; 

public void init() throws ServletException
{
	url_login_page = this.getInitParameter(URL_LOGIN_PAGE);
	if (url_login_page == null || url_login_page.length() == 0)
	{
		url_login_page = this.getServletContext().getInitParameter(URL_LOGIN_PAGE);
		if (url_login_page == null || url_login_page.length() == 0)
		{
			throw new ServletException("No parameter url-login-page specified");
		}
	}
}

public void doGet(HttpServletRequest req, HttpServletResponse resp)
	     throws ServletException, java.io.IOException
{
	String token = WPBLocalAuthentication.getTokenCookie(req);
	if (token != null && token.length()>0)
	{
		String path = new String(CmsBase64Utility.fromSafePathBase64(token));
		File file = new File(path);
		if (file.exists())
		{
			file.delete();
		}
		// delete the cookie
		Cookie cookie = new Cookie(WPBLocalAuthentication.TOKEN_COOKIE, "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		resp.addCookie(cookie);
	}
	
	resp.sendRedirect(url_login_page);
}

}
