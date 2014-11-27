package com.webpagebytes.cms.appinterfaces;

import com.webpagebytes.cms.exception.WPBException;

public interface WPBContentService {
	public WPBModel createModel(String language, String country) throws WPBException;
	public WPBModel createModel() throws WPBException;
	public WPBContentProvider getContentProvider() throws WPBException;
}
