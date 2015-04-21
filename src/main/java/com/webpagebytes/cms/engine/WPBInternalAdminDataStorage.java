package com.webpagebytes.cms.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.engine.WPBAdminDataStorageListener.AdminDataStorageOperation;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBInternalAdminDataStorage implements WPBAdminDataStorage {

	private WPBAdminDataStorage instance;
	private Vector<WPBAdminDataStorageListener> storageListeners = new Vector<WPBAdminDataStorageListener>();
	private boolean notificationsFlag = true;

	
	WPBInternalAdminDataStorage(WPBAdminDataStorage instance)
	{
		this.instance = instance;
	}
	
	@Override
	public void initialize(Map<String, String> params) throws WPBIOException {
		instance.initialize(params);		
	}
	
	@Override
	public <T> void delete(String recordid, Class<T> dataClass)
			throws WPBIOException {
		instance.delete(recordid, dataClass);		
		notifyOperation(null, AdminDataStorageOperation.DELETE_RECORD, dataClass);			
	}
	
	@Override
	public <T> void delete(Class<T> dataClass, String property,
			AdminQueryOperator operator, Object parameter)
			throws WPBIOException {
		instance.delete(dataClass, property, operator, parameter);
		notifyOperation(null, AdminDataStorageOperation.DELETE_RECORDS, dataClass);				
	}
	
	@Override
	public <T> List<T> getAllRecords(Class<T> dataClass) throws WPBIOException {
		return instance.getAllRecords(dataClass);
	}
	
	@Override
	public <T> List<T> getAllRecords(Class<T> dataClass, String property,
			AdminSortOperator operator) throws WPBIOException {
		return instance.getAllRecords(dataClass, property, operator);
	}
	
	@Override
	public <T> T add(T record) throws WPBIOException {
		T result = instance.add(record);
		notifyOperation(result, AdminDataStorageOperation.CREATE_RECORD, record.getClass());			
		return result;
	}
	
	@Override
	public <T> T addWithKey(T record) throws WPBIOException {
		T result = instance.addWithKey(record);
		notifyOperation(result, AdminDataStorageOperation.CREATE_RECORD, record.getClass());			
		return result;
	}
	
	@Override
	public <T> T get(String recordid, Class<T> dataClass) throws WPBIOException {
		return instance.get(recordid, dataClass);
	}
	
	@Override
	public <T> T update(T record) throws WPBIOException {
		T result = instance.update(record);
		notifyOperation(result, AdminDataStorageOperation.UPDATE_RECORD, record.getClass());	
		return result;
	}
	
	@Override
	public <T> List<T> query(Class<T> dataClass, String property,
			AdminQueryOperator operator, Object parameter)
			throws WPBIOException {
		return instance.query(dataClass, property, operator, parameter);
	}
	@Override
	public <T> List<T> queryEx(Class<T> dataClass, Set<String> propertyNames,
			Map<String, AdminQueryOperator> operators,
			Map<String, Object> values) throws WPBIOException {
		return instance.queryEx(dataClass, propertyNames, operators, values);
	}

	@Override
	public <T> List<T> queryWithSort(Class<T> dataClass, String property,
			AdminQueryOperator operator, Object parameter, String sortProperty,
			AdminSortOperator sortOperator) throws WPBIOException {
		return instance.queryWithSort(dataClass, property, operator, parameter, sortProperty, sortOperator);
	}

    public void stopNotifications() {
        notificationsFlag = false;
        
    }

    public void startNotifications() {
        notificationsFlag = true;
    }

    public boolean isNotificationActive() {
        return notificationsFlag;
    }
    
	public void addStorageListener(WPBAdminDataStorageListener listener)
	{
		synchronized (storageListeners)
		{
			storageListeners.add(listener);
		}
	}
	
	public void removeStorageListener(WPBAdminDataStorageListener listener)
	{
		synchronized (storageListeners)
		{
			for(int i=0; i< storageListeners.size(); i++)
			{
				if (storageListeners.get(i) == listener)
				{
					storageListeners.remove(i);
					return;
				}
			}
		}
	}
	
	protected<T> void notifyOperation(T obj, WPBAdminDataStorageListener.AdminDataStorageOperation operation, Class<? extends Object> type)
	{
	    if (notificationsFlag)
	    {
    		synchronized (storageListeners)
    		{
    			for(int i=0; i< storageListeners.size(); i++)
    			{
    				storageListeners.get(i).notify(obj, operation, type);
    			}
    		}
	    }
	}
	
	@Override
	public <T> void deleteAllRecords(Class<T> dataClass) throws WPBIOException {
		instance.deleteAllRecords(dataClass);
		notifyOperation(null, AdminDataStorageOperation.DELETE_RECORDS, dataClass);				
	}
	
	public String getUniqueId() {
		return UUID.randomUUID().toString();
	}
}
