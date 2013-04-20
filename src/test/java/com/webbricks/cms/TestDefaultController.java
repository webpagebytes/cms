package com.webbricks.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbricks.controllers.WBController;
import com.webbricks.exception.WBException;

public class TestDefaultController extends WBController {
	String uriValue;
	protected void setJsonResponseType(HttpServletResponse response)
	{
		response.setContentType("application/json");
	}
	public void test(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		setJsonResponseType(response);
		setUriValue(requestUri);
	}
	public String getUriValue() {
		return uriValue;
	}
	public void setUriValue(String uriValue) {
		this.uriValue = uriValue;
	}
	

}
