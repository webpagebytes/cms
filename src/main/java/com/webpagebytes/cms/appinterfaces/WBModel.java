package com.webpagebytes.cms.appinterfaces;

import java.util.Map;
import java.util.Set;

public class WBModel {
	
	public static final String GLOBALS_KEY = "wpbGlobals";
	public static final String REQUEST_KEY = "wpbRequest";
	public static final String PAGE_PARAMETERS_KEY = "wpbPageParams";
	public static final String URI_PARAMETERS_KEY = "wpbUriParams";
	
	public static final String PAGE_CONTROLLER_MODEL_KEY = "wpbPageModel";
	public static final String URI_CONTROLLER_MODEL_KEY = "wpbUriModel";
	
	public static final String LOCALE_KEY = "wpbLocale";
	public static final String LOCALE_LANGUAGE_KEY = "wpbLocaleLanguage";
	public static final String LOCALE_COUNTRY_KEY = "wpbLocaleCountry";
	public static final String LOCALE_MESSAGES = "wpbMessages";
	public static final String MODULE_DIRECTIVE = "wpbModule";
	public static final String IMAGE_DIRECTIVE = "wpbImage";
	public static final String ARTICLE_DIRECTIVE = "wpbArticle";
	public static final String TEXT_FORMAT_DIRECTIVE = "wpbFormatText";
	public static final String FORMAT_TEXT_METHOD = "wpbFormatText";
	
	
	public static final String GLOBAL_PROTOCOL = "WPB_GLOBAL_PROTOCOL";
	public static final String GLOBAL_DOMAIN = "WPB_GLOBAL_DOMAIN";
	public static final String GLOBAL_URI_PREFIX = "WPB_GLOBAL_URI_PREFIX";
	public static final String GLOBAL_BASE_URL = "WPB_GLOBAL_BASE_URL";
	

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
