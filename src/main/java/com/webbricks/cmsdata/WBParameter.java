package com.webbricks.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;

public class WBParameter implements Serializable {

	@AdminFieldKey
	private Long key;

	@AdminFieldStore
	private String name;
	
	@AdminFieldStore
	private String value;

	@AdminFieldStore
	private Date lastModified;

	@AdminFieldStore
	private Long ownerExternalKey;
	
	@AdminFieldStore
	private Integer overwriteFromUrl;
	
	@AdminFieldStore
	private Integer localeType;
	
	@AdminFieldStore
	private Long externalKey;
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
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

	public Long getOwnerExternalKey() {
		return ownerExternalKey;
	}

	public void setOwnerExternalKey(Long keyOwner) {
		this.ownerExternalKey = keyOwner;
	}

	public Integer getOverwriteFromUrl() {
		return overwriteFromUrl;
	}

	public void setOverwriteFromUrl(Integer overwriteFromUrl) {
		this.overwriteFromUrl = overwriteFromUrl;
	}

	public Long getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(Long externalKey) {
		this.externalKey = externalKey;
	}

	public Integer getLocaleType() {
		return localeType;
	}

	public void setLocaleType(Integer localeType) {
		this.localeType = localeType;
	}

	
	
}
