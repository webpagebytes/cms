package com.webpagebytes.cms;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;
import com.webpagebytes.cms.datautility.AdminFieldTextStore;

public class DummyClientS {
	@AdminFieldKey
	protected String id;
	@AdminFieldStore
	protected Integer age;
	@AdminFieldStore
	private String name;
	@AdminFieldStore
	public Long balance;
	@AdminFieldStore
	public byte binary;
	@AdminFieldTextStore
	private String summary;
	@AdminFieldStore
	private Boolean isBoolean;
	@AdminFieldStore
	private int width;
	@AdminFieldStore
	private long height;
	
	
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
