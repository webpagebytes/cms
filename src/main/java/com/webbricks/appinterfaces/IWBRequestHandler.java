package com.webbricks.appinterfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IWBRequestHandler {
	public void handleRequest(HttpServletRequest request, 
							  HttpServletResponse response, 
							  WBModel model,
							  WBForward forward);
}
