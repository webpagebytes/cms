package com.webpagebytes.cms.appinterfaces;


import javax.servlet.http.HttpServletRequest;

public interface WBPageModelProvider {
	public void populatePageModel(HttpServletRequest request, WBModel model);
	public void populatePageModel(WBModel model);
}
