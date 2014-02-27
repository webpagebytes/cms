package com.webbricks.appinterfaces;


import javax.servlet.http.HttpServletRequest;

public interface IPageModelProvider {
	public void getPageModel(HttpServletRequest request, WBModel model);
}
