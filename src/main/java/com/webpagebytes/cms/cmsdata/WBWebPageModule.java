package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;
import com.webpagebytes.cms.datautility.WPBAdminFieldTextStore;

public class WBWebPageModule implements Serializable {
	@WPBAdminFieldKey
	private Long privkey;

	@WPBAdminFieldStore
	private String name;

	@WPBAdminFieldStore
	private Date lastModified;

	@WPBAdminFieldTextStore
	private String htmlSource;
	
	@WPBAdminFieldStore
	private Integer isTemplateSource;

	@WPBAdminFieldStore
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

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}

	public Integer getIsTemplateSource() {
		return isTemplateSource;
	}

	public void setIsTemplateSource(Integer isTemplateSource) {
		this.isTemplateSource = isTemplateSource;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

}
