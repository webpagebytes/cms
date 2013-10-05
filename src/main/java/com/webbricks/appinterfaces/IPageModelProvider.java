package com.webbricks.appinterfaces;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface IPageModelProvider {
	public void getPageModel(HttpServletRequest request, Map<String, Object> model);
}
