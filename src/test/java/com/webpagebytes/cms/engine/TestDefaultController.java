package com.webpagebytes.cms.engine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.controllers.Controller;
import com.webpagebytes.cms.exception.WPBException;

public class TestDefaultController extends Controller {
	String uriValue;
	protected void setJsonResponseType(HttpServletResponse response)
	{
		response.setContentType("application/json");
	}
	public void test(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
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
