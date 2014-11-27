package com.webpagebytes.cms.appinterfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WPBRequestHandler {
	public void initialize(WPBContentProvider contentProvider);
	
	public void handleRequest(HttpServletRequest request, 
							  HttpServletResponse response, 
							  WPBModel model,
							  WPBForward forward);
}
