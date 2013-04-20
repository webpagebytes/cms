package com.webbricks.cms;

import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;

public class DummyPrivateClientS {
	@AdminFieldKey
	protected String id;
	@AdminFieldStore
	protected int age;
	@AdminFieldStore
	private String name;
	@AdminFieldStore
	private long balance;
	@AdminFieldStore
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
