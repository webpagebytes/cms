package com.webbricks.datautility.local;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.AdminDataStorage.AdminSortOperator;
import com.webbricks.exception.WBIOException;

public class WBLocalAdminDataStorage implements AdminDataStorage {

	
	public void delete(String recordid, Class dataClass) throws WBIOException
	{
		
	}
	
	public void delete(Long recordid, Class dataClass) throws WBIOException
	{
		
	}
	
	public void delete(Class dataClass, String property, AdminQueryOperator operator, Object parameter) throws WBIOException
	{
		
	}
	
	public<T> List<T> getAllRecords(Class dataClass) throws WBIOException
	{
		ArrayList<T> result = new ArrayList<T>();
		return result;
	}
	
	public<T> List<T> getAllRecords(Class dataClass, String property, AdminSortOperator operator) throws WBIOException
	{
		ArrayList<T> result = new ArrayList<T>();
		return result;
	}

	public<T> T add(T t) throws WBIOException
	{
		return null;
	}

	public<T> T get(Long dataid, Class dataClass) throws WBIOException
	{
		return null;
	}
	
	public<T> T get(String dataid, Class dataClass) throws WBIOException
	{
		return null;
	}
	
	public<T> T update(T data) throws WBIOException
	{
		return null;
	}
	
	public<T> List<T> query(Class dataClass, String property, AdminQueryOperator operator, Object parameter) throws WBIOException
	{
		ArrayList<T> result = new ArrayList<T>();
		return result;
	}
	
	public<T> List<T> queryEx(Class dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values) throws WBIOException
	{
		ArrayList<T> result = new ArrayList<T>();
		return result;
	}

	public<T> List<T> queryWithSort(Class dataClass, String property, AdminQueryOperator operator, Object parameter, String sortProperty, AdminSortOperator sortOperator) throws WBIOException
	{
		ArrayList<T> result = new ArrayList<T>();
		return result;
	}
	
	public<T> List<T> queryExWithSort(Class dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values, String sortProperty, AdminSortOperator sortOperator) throws WBIOException
	{
		ArrayList<T> result = new ArrayList<T>();
		return result;
	}	
	

	public void addStorageListener(AdminDataStorageListener listener)
	{
	}

	
	public void removeStorageListener(AdminDataStorageListener listener)
	{
		
	}
	
	public String getUploadUrl(String returnUrl)
	{
		return "";
	}
	
	public void deleteAllRecords(Class dataClass)
	{
		
	}
	
	public String getUniqueId()
	{
		return java.util.UUID.randomUUID().toString();
	}

}
