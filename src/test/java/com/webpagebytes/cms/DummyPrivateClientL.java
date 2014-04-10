package com.webpagebytes.cms;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;

public class DummyPrivateClientL {
	@AdminFieldKey
	protected Long id;
	@AdminFieldStore
	protected int age;
	@AdminFieldStore
	private String name;
	@AdminFieldStore
	private long balance;
	@AdminFieldStore
	private byte binary;
	private Long getId() {
		return id;
	}
	private void setId(Long id) {
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
