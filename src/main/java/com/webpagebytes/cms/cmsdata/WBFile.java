package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;

public class WBFile implements Serializable {

	@AdminFieldKey
	private Long key;

	@AdminFieldStore
	private String name;

	@AdminFieldStore
	private String externalKey;
	
	@AdminFieldStore
	private String blobKey;
	
	@AdminFieldStore
	private Date lastModified;
	
	@AdminFieldStore
	private String contentType;
	
	@AdminFieldStore
	private String adjustedContentType;
	
	@AdminFieldStore
	private String shortType;	

	@AdminFieldStore
	private Long size;
	
	@AdminFieldStore
	private String fileName;
	
	@AdminFieldStore
	private Long hash;

	@AdminFieldStore
	private String publicUrl;

	@AdminFieldStore
	private String thumbnailPublicUrl;

	@AdminFieldStore
	private String thumbnailBlobKey;

	
	
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

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	public String getBlobKey() {
		return blobKey;
	}

	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getShortType() {
		return shortType;
	}

	public void setShortType(String shortType) {
		this.shortType = shortType;
	}

	public String getAdjustedContentType() {
		return adjustedContentType;
	}

	public void setAdjustedContentType(String adjustedContentType) {
		this.adjustedContentType = adjustedContentType;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}

	public String getPublicUrl() {
		return publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

	public String getThumbnailPublicUrl() {
		return thumbnailPublicUrl;
	}

	public void setThumbnailPublicUrl(String thumbnailPublicUrl) {
		this.thumbnailPublicUrl = thumbnailPublicUrl;
	}

	public String getThumbnailBlobKey() {
		return thumbnailBlobKey;
	}

	public void setThumbnailBlobKey(String thumbnailBlobKey) {
		this.thumbnailBlobKey = thumbnailBlobKey;
	}


}
