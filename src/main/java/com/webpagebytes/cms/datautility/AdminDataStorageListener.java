package com.webpagebytes.cms.datautility;

public interface AdminDataStorageListener<T> {

	enum AdminDataStorageOperation
	{
		CREATE_RECORD,
		UPDATE_RECORD,
		DELETE_RECORD,
		DELETE_RECORDS
		
	}
	public void notify (T t, AdminDataStorageOperation o, Class type);
}
