package com.webpagebytes.cms.appinterfaces;

import com.webpagebytes.cms.exception.WBException;

public interface WPBContentService {
	public WPBModel createModel(String language, String country) throws WBException;
	public WPBModel createModel() throws WBException;
	public WPBContentProvider getContentProvider() throws WBException;
}
