package com.webpagebytes.cms.appinterfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WBRequestHandler {
	public void initialize(WBContentProvider contentProvider);
	
	public void handleRequest(HttpServletRequest request, 
							  HttpServletResponse response, 
							  WBModel model,
							  WBForward forward);
}
