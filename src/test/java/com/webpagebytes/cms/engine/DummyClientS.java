package com.webpagebytes.cms.engine;

import com.webpagebytes.cms.cmsdata.WPBAdminFieldKey;
import com.webpagebytes.cms.cmsdata.WPBAdminFieldStore;
import com.webpagebytes.cms.cmsdata.WPBAdminFieldTextStore;

public class DummyClientS {
	@WPBAdminFieldKey
	protected String id;
	@WPBAdminFieldStore
	protected Integer age;
	@WPBAdminFieldStore
	private String name;
	@WPBAdminFieldStore
	public Long balance;
	@WPBAdminFieldStore
	public byte binary;
	@WPBAdminFieldTextStore
	private String summary;
	@WPBAdminFieldStore
	private Boolean isBoolean;
	@WPBAdminFieldStore
	private Integer width;
	@WPBAdminFieldStore
	private Long height;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public byte getBinary() {
		return binary;
	}
	public void setBinary(byte binary) {
		this.binary = binary;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Boolean getIsBoolean() {
		return isBoolean;
	}
	public void setIsBoolean(Boolean isBoolean) {
		this.isBoolean = isBoolean;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public long getHeight() {
		return height;
	}
	public void setHeight(long height) {
		this.height = height;
	}
	
}
