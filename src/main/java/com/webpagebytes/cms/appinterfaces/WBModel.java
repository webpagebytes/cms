package com.webpagebytes.cms.appinterfaces;

import java.util.Map;
import java.util.Set;

public class WBModel {
	
	public static final String GLOBALS_KEY = "wbGlobals";
	public static final String REQUEST_KEY = "wbRequest";
	public static final String PAGE_PARAMETERS_KEY = "wbPageParams";
	public static final String URI_PARAMETERS_KEY = "wbUriParams";
	
	public static final String PAGE_CONTROLLER_MODEL_KEY = "wbPageModel";
	public static final String URI_CONTROLLER_MODEL_KEY = "wbUriModel";
	
	public static final String LOCALE_KEY = "wbLocale";
	public static final String LOCALE_LANGUAGE_KEY = "wbLocaleLanguage";
	public static final String LOCALE_COUNTRY_KEY = "wbLocaleCountry";
	public static final String LOCALE_MESSAGES = "wbMessages";
	public static final String MODULE_DIRECTIVE = "wbModule";
	public static final String IMAGE_DIRECTIVE = "wbImage";
	public static final String ARTICLE_DIRECTIVE = "wbArticle";
	public static final String TEXT_FORMAT_DIRECTIVE = "wbFormatText";
	public static final String FORMAT_TEXT_METHOD = "wbFormatText";
	
	
	public static final String GLOBAL_PROTOCOL = "WB_GLOBAL_PROTOCOL";
	public static final String GLOBAL_DOMAIN = "WB_GLOBAL_DOMAIN";
	public static final String GLOBAL_URI_PREFIX = "WB_GLOBAL_URI_PREFIX";

	protected WBCmsModel cmsModel = new WBCmsModel();
	protected WBCustomModel customModel = new WBCustomModel();
	
	public WBCmsModel getCmsModel()
	{
		return cmsModel;
	}
	public WBCustomModel getCmsCustomModel()
	{
		return customModel;
	}
	
	public void transferModel(Map<String, Object> rootObject)
	{
		Set<String> keys = cmsModel.keySet();
		for(String key: keys)
		{
			rootObject.put(key, cmsModel.get(key));
		}
				
	}
}
