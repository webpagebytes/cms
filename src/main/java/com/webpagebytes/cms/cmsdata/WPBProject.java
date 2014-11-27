package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;

public class WPBProject implements Serializable{
	public static final String PROJECT_KEY = "wbprojectid";
	
	@WPBAdminFieldKey
	private String privkey;

	@WPBAdminFieldStore
	private Date lastModified;
	
	@WPBAdminFieldStore
	private String supportedLanguages;

	@WPBAdminFieldStore
	private String defaultLanguage;
	
	public String getPrivkey() {
		return privkey;
	}

	public void setPrivkey(String key) {
		this.privkey = key;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getSupportedLanguages()
	{
		return supportedLanguages;
	}
	
	public Set<String> getSupportedLanguagesSet() {
		if (supportedLanguages != null)
		{
			String[] langs = supportedLanguages.split(",");
			Set<String> supportedLanguagesSet = new HashSet<String>();
			for(String lang: langs)
			{
				if (lang.length()>0) supportedLanguagesSet.add(lang);
			}
			return supportedLanguagesSet;
		} 
		return null;

	}

	public void setSupportedLanguages(String supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	
	
}
