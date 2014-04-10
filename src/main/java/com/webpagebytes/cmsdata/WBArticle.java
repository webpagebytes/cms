package com.webpagebytes.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.datautility.AdminFieldKey;
import com.webpagebytes.datautility.AdminFieldStore;
import com.webpagebytes.datautility.AdminFieldTextStore;

public class WBArticle implements Serializable {
	@AdminFieldKey
	private Long key;
	
	@AdminFieldStore
	private Date lastModified;

	@AdminFieldStore
	private String title;

	@AdminFieldTextStore
	private String htmlSource;
		
	@AdminFieldStore
	private String externalKey;

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
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
