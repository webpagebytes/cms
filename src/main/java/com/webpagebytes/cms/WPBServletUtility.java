package com.webpagebytes.cms;

import javax.servlet.http.HttpServlet;

public class WPBServletUtility {

	public String getInitParameter(String name, HttpServlet servlet)
	{
		return servlet.getInitParameter(name);
	}
	public String getContextParameter(String name, HttpServlet servlet)
	{
		return servlet.getServletContext().getInitParameter(name);
	}
	public String getContextPath(HttpServlet servlet)
	{
		return servlet.getServletContext().getContextPath();
	}
}
