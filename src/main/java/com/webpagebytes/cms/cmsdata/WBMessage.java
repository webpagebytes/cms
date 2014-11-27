package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;
import com.webpagebytes.cms.datautility.WPBAdminFieldTextStore;

public class WBMessage implements Serializable {

	@WPBAdminFieldKey
	private Long privkey;

	@WPBAdminFieldStore
	private String name;

	@WPBAdminFieldStore
	private String externalKey;

	@WPBAdminFieldTextStore
	private String value;

	@WPBAdminFieldStore
	private String lcid;

	@WPBAdminFieldStore
	private Integer isTranslated;

	@WPBAdminFieldStore
	private Date lastModified;
	
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

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLcid() {
		return lcid;
	}

	public void setLcid(String lcid) {
		this.lcid = lcid;
	}

	public Integer getIsTranslated() {
		return isTranslated;
	}

	public void setIsTranslated(Integer isTranslated) {
		this.isTranslated = isTranslated;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	
}
