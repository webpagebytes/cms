package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;
import com.webpagebytes.cms.datautility.AdminFieldTextStore;

public class WBArticle implements Serializable {
	@AdminFieldKey
	private Long privkey;
	
	@AdminFieldStore
	private Date lastModified;

	@AdminFieldStore
	private String title;

	@AdminFieldTextStore
	private String htmlSource;
		
	@AdminFieldStore
	private String externalKey;

	public Long getPrivkey() {
		return privkey;
	}

	public void setPrivkey(Long key) {
		this.privkey = key;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	
}
