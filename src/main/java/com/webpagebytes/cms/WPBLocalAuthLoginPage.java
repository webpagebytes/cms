package com.webpagebytes.cms;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBLocalAuthLoginPage extends HttpServlet {

private static final long serialVersionUID = 1L;

private static final String login_page = " " +
"<!DOCTYPE html>" +
"<html><head><style>" +
".box {" + 
"border: 1px solid #A0A0A0;" +
"background-color: #E0E0E0;" +
"width: 400px;" +
"margin: 30px auto;} \n" +
".line {" +
"padding: 20px 0px 10px;" +
"margin: 0px auto;" +
"width: 90%;" +
"text-align:center;} \n" +
".error {" +
"margin: 0px auto;" +
"width: 90%;" +
"color: #FF0000" +
"text-align:center;} \n" +
"</style></head>" +
"<body><form method='POST' action='STR_ACTION'>" +
"<div class='box'>" +
"<div class='line'> Demo autentication. Do not use it in production! </div>" +
"<div class='line'> User name <input type='text' name='userName'> </div>" +
"<div class='error'> STR_ERROR </div>" +
"<div class='line'> <input type='submit' Value='Login'> </div>" +
"</div></form>" +
"</body></html>";
		
private static final String ERROR_NO_USER = "User name cannot be empty";
private static final String ERROR_INVALID = "Invalid characters for user name";

private String uri_login = "";
private String uri_login_redirect = "";
public static final String URL_LOGIN_POST_CONFIG = "url-login-post"; 
public static final String URL_LOGIN_REDIRECT_CONFIG = "url-login-redirect-success";
public static final String DIR_TEMP_USERS = "wpbTempUsers"; 

public void init() throws ServletException
{
	uri_login = this.getInitParameter(URL_LOGIN_POST_CONFIG);
	if (uri_login == null || uri_login.length() == 0)
	{
		uri_login =  this.getServletContext().getInitParameter(URL_LOGIN_POST_CONFIG);
		if (uri_login == null || uri_login.length() == 0)
		{
			throw new ServletException("No parameter uri-login specified");
		}
	}
	
	uri_login_redirect = this.getInitParameter(URL_LOGIN_REDIRECT_CONFIG);
	if (uri_login_redirect == null || uri_login_redirect.length() == 0)
	{
		uri_login_redirect = this.getServletContext().getInitParameter(URL_LOGIN_REDIRECT_CONFIG);
		if (uri_login_redirect == null || uri_login_redirect.length() == 0)
		{
			throw new ServletException("No parameter uri_login_redirect specified");
		}
	}
}
public void doGet(HttpServletRequest req, HttpServletResponse resp)
	     throws ServletException, java.io.IOException
{
	String html = login_page.replaceAll("STR_ACTION", uri_login).replaceAll("STR_ERROR", "");
	resp.getOutputStream().write(html.getBytes());
	resp.flushBuffer();
}

private String getUserLoginFilePath(String userName)
{
	String tempName = System.getProperty("java.io.tmpdir");
	if (! tempName.endsWith(File.separator))
	{
		tempName += File.separator;
	}
	tempName = tempName.concat(DIR_TEMP_USERS);
	File usersDir = new File(tempName);
	if (!usersDir.exists())
	{
		usersDir.mkdir();
	}
	tempName = tempName.concat(File.separator).concat(userName);
	return tempName;
}

public void doPost(HttpServletRequest req, HttpServletResponse resp)
	     throws ServletException, java.io.IOException
{
	String userName = req.getParameter("userName");
	if (userName == null || userName.length() == 0)
	{
		String html = login_page.replaceAll("STR_ACTION", uri_login).replaceAll("STR_ERROR", ERROR_NO_USER);
		resp.getOutputStream().write(html.getBytes());
		resp.flushBuffer();		
		return;
	}
	if (!userName.matches("[0-9a-zA-Z@_.-]*"))
	{
		String html = login_page.replaceAll("STR_ACTION", uri_login).replaceAll("STR_ERROR", ERROR_INVALID);
		resp.getOutputStream().write(html.getBytes());
		resp.flushBuffer();		
		return;
	}
	
	String loginFilePath = getUserLoginFilePath(userName);
	File file = new File(loginFilePath);
	if (! file.exists())
	{
		file.createNewFile();
	}
	Cookie cookie = new Cookie(WPBLocalAuthentication.TOKEN_COOKIE, CmsBase64Utility.toSafePathBase64(loginFilePath.getBytes()));
	cookie.setPath("/");
	cookie.setMaxAge(1000000);
	resp.addCookie(cookie);
	
	resp.sendRedirect(uri_login_redirect);
}

}
