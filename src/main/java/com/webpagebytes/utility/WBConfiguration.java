package com.webpagebytes.utility;

import java.util.Map;

public interface WBConfiguration {
	enum SECTION
	{
		SECTION_CACHE,
		SECTION_DATASTORAGE,
		SECTION_FILESTORAGE,
		SECTION_IMAGEPROCESSOR
	};

	public String getSectionClassFactory(SECTION section);
	public Map<String,String> getSectionParams(SECTION section);
}
