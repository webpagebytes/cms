package com.webpagebytes.cms.utility;

import java.util.Map;

public interface CmsConfiguration {
	enum WPBSECTION
	{
		SECTION_CACHE,
		SECTION_DATASTORAGE,
		SECTION_FILESTORAGE,
		SECTION_IMAGEPROCESSOR,
		SECTION_MODEL_CONFIGURATOR
	};

	public String getSectionClassFactory(WPBSECTION section);
	public Map<String,String> getSectionParams(WPBSECTION section);
}
