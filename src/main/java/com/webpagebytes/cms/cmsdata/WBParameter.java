package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;

public class WBParameter implements Serializable {
	public static final int PARAMETER_NO_TYPE = 0;
	public static final int PARAMETER_LOCALE_LANGUAGE = 1;
	public static final int PARAMETER_LOCALE_COUNTRY = 2;
	
	@AdminFieldKey
	private Long privkey;

	@AdminFieldStore
	private String name;
	
	@AdminFieldStore
	private String value;

	@AdminFieldStore
	private Date lastModified;

	@AdminFieldStore
	private String ownerExternalKey;
	
	@AdminFieldStore
	private Integer overwriteFromUrl;
	
	@AdminFieldStore
	private Integer localeType;
	
	@AdminFieldStore
	private String externalKey;
	
	public Long getPrivkey() {
		return privkey;
	}

	public void setPrivkey(Long key) {
		this.privkey = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getOwnerExternalKey() {
		return ownerExternalKey;
	}

	public void setOwnerExternalKey(String keyOwner) {
		this.ownerExternalKey = keyOwner;
	}

	public Integer getOverwriteFromUrl() {
		return overwriteFromUrl;
	}

	public void setOverwriteFromUrl(Integer overwriteFromUrl) {
		this.overwriteFromUrl = overwriteFromUrl;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	public Integer getLocaleType() {
		return localeType;
	}

	public void setLocaleType(Integer localeType) {
		this.localeType = localeType;
	}

	
	
}
