package com.webbricks.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;

public class WBProject implements Serializable{
	public static final String PROJECT_KEY = "wbprojectid";
	
	@AdminFieldKey
	private String key;

	@AdminFieldStore
	private Date lastModified;
	
	@AdminFieldStore
	private String supportedLanguages;

	@AdminFieldStore
	private String defaultLanguage;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getSupportedLanguages() {
		return supportedLanguages;
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
