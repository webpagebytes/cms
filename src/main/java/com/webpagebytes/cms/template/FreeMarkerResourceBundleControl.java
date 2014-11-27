package com.webpagebytes.cms.template;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.webpagebytes.cms.cache.WPBMessagesCache;

public class FreeMarkerResourceBundleControl extends ResourceBundle.Control {

	protected WPBMessagesCache messageCache;
	
	FreeMarkerResourceBundleControl(WPBMessagesCache messageCache)
	{
		this.messageCache = messageCache;
	}
	
    public List<String> getFormats(String baseName) {
        if (baseName == null)
            throw new NullPointerException();
        return FORMAT_PROPERTIES;
    }

	public ResourceBundle newBundle(String baseName,
            Locale locale,
            String format,
            ClassLoader loader,
            boolean reload) throws IllegalAccessException, InstantiationException, IOException 
    {
		return new CmsResourceBundle(messageCache, locale);
	}
	
	public long getTimeToLive(String baseName,
            Locale locale)
	{
		return 0;
	}
	public boolean needsReload(String baseName,
            Locale locale,
            String format,
            ClassLoader loader,
            ResourceBundle bundle,
            long loadTime)
	{
		CmsResourceBundle wbresource = (CmsResourceBundle) bundle;
		if (!wbresource.getFingerPrint().equals(messageCache.getFingerPrint(locale)))
		{
			return true;
		}
		return false;
	}
}
