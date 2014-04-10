package com.webpagebytes.cms;

import javax.servlet.http.HttpServlet;

public class WBServletUtility {

	public String getInitParameter(String name, HttpServlet servlet)
	{
		return servlet.getInitParameter(name);
	}
}
