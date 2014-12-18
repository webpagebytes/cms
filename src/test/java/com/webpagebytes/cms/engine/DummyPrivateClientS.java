package com.webpagebytes.cms.engine;

import com.webpagebytes.cms.cmsdata.WPBAdminFieldKey;
import com.webpagebytes.cms.cmsdata.WPBAdminFieldStore;

public class DummyPrivateClientS {
	@WPBAdminFieldKey
	protected String id;
	@WPBAdminFieldStore
	protected int age;
	@WPBAdminFieldStore
	private String name;
	@WPBAdminFieldStore
	private long balance;
	@WPBAdminFieldStore
	private byte binary;
	private String getId() {
		return id;
	}
	private void setId(String id) {
		this.id = id;
	}
	private int getAge() {
		return age;
	}
	private void setAge(int age) {
		this.age = age;
	}
	private String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private long getBalance() {
		return balance;
	}
	private void setBalance(long balance) {
		this.balance = balance;
	}
	private byte getBinary() {
		return binary;
	}
	private void setBinary(byte binary) {
		this.binary = binary;
	}
}
