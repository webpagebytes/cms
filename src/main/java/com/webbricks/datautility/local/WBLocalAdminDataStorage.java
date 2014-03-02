package com.webbricks.datautility.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webbricks.datautility.local.WBLocalDataStoreDao.WBLocalQueryOperator;
import com.webbricks.datautility.local.WBLocalDataStoreDao.WBLocalSortDirection;
import com.webbricks.exception.WBIOException;

public class WBLocalAdminDataStorage implements AdminDataStorage {
	private static final Logger log = Logger.getLogger(WBLocalAdminDataStorage.class.getName());
	private static final String KEY_FILED_NAME = "key";
	private Vector<AdminDataStorageListener> storageListeners = new Vector<AdminDataStorageListener>();
	
	WBLocalDataStoreDao localDataStorageDao = new WBLocalDataStoreDao("~/test");
	
	private WBLocalDataStoreDao.WBLocalQueryOperator adminOperatorToLocalOperator(AdminQueryOperator adminOperator)
	{
		switch (adminOperator)
		{
		case LESS_THAN:
			return WBLocalQueryOperator.LESS_THAN;
		case GREATER_THAN:
			return WBLocalQueryOperator.GREATER_THAN;
		case EQUAL:
			return WBLocalQueryOperator.EQUAL;
		case GREATER_THAN_OR_EQUAL:
			return WBLocalQueryOperator.GREATER_THAN_OR_EQUAL;
		case LESS_THAN_OR_EQUAL:
			return WBLocalQueryOperator.LESS_THAN_OR_EQUAL;
		case NOT_EQUAL:
			return WBLocalQueryOperator.NOT_EQUAL;
		default:
			return null;
		}
	}
	
	private WBLocalDataStoreDao.WBLocalSortDirection adminDirectionToLocalDirection(AdminSortOperator sortOperator)
	{
		switch (sortOperator)
		{
		case ASCENDING:
			return WBLocalSortDirection.ASCENDING;
		case DESCENDING:
			return WBLocalSortDirection.DESCENDING;
		case NO_SORT:
			return WBLocalSortDirection.NO_SORT;
		default:
			return null;
		}
	}
	
	public void delete(String recordid, Class dataClass) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "delete record %s", recordid);
			localDataStorageDao.deleteRecord(dataClass, "key", recordid);			
			Object obj = dataClass.newInstance();
			localDataStorageDao.setObjectProperty(obj, "key", recordid);
			notifyOperation(obj, AdminDataStorageOperation.DELETE);			

		} catch (Exception e)
		{
			throw new WBIOException("Cannot delete record " + recordid, e);
		}
	}
	
	public void delete(Long recordid, Class dataClass) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "delete record %d", recordid);
			localDataStorageDao.deleteRecord(dataClass, "key", recordid);
			Object obj = dataClass.newInstance();
			localDataStorageDao.setObjectProperty(obj, "key", recordid);
			notifyOperation(obj, AdminDataStorageOperation.DELETE);			
		} catch (Exception e)
		{
			throw new WBIOException("Cannot delete record " + recordid, e);
		}		
	}
	
	public void delete(Class dataClass, String property, AdminQueryOperator operator, Object parameter) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records with property condition %s ", property);		
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			operators.put(property, adminOperatorToLocalOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			localDataStorageDao.deleteRecords(dataClass, properties, operators, values);
			Object obj = dataClass.newInstance();
			notifyOperation(obj, AdminDataStorageOperation.DELETE);				
		} catch (Exception e)
		{
			throw new WBIOException("Cannot delete records ", e);
		}
	}
	
	public<T> List<T> getAllRecords(Class dataClass) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "get all record %s", dataClass.getSimpleName());			
			List<T> result = (List<T>) localDataStorageDao.getAllRecords(dataClass);
			return result;
		} catch (Exception e)
		{
			throw new WBIOException("cannot get all records", e);
		} 
	}
	
	public<T> List<T> getAllRecords(Class dataClass, String property, AdminSortOperator sortOperator) throws WBIOException
	{
		try
		{
			Object [] logObjects = { dataClass.getSimpleName(), property};
			log.log(Level.INFO, "get all record %s with condition on property %s", logObjects);			
			
			Set<String> properties = new HashSet();
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			Map<String, Object> values = new HashMap<String, Object>();
			List<T> result = (List<T>)localDataStorageDao.queryWithSort(dataClass, properties, operators, values, property, adminDirectionToLocalDirection(sortOperator));
			return result;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot get all records with sorting", e);
		}

	}

	public<T> T add(T t) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "add record for class %s", t.getClass().getSimpleName());			
			T res = localDataStorageDao.addRecord(t, KEY_FILED_NAME);			
			notifyOperation(t, AdminDataStorageOperation.CREATE);			
			return res;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot add new record", e);
		}
	}

	public<T> T addWithKey(T t) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "add record with key for class %s", t.getClass().getSimpleName());			
			T res = localDataStorageDao.addRecordWithKey(t, KEY_FILED_NAME);
			notifyOperation(t, AdminDataStorageOperation.CREATE);			
			return res;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot add new record", e);
		}
	}

	public<T> T get(Long dataid, Class dataClass) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "get record for key %d", dataid);			
			return (T) localDataStorageDao.getRecord(dataClass, KEY_FILED_NAME, dataid);
		} catch (Exception e)
		{
			throw new WBIOException("Cannot add new record", e);
		}
	}
	
	public<T> T get(String dataid, Class dataClass) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "get record for key %s", dataid);
			return (T) localDataStorageDao.getRecord(dataClass, KEY_FILED_NAME, dataid);
		} catch (Exception e)
		{
			throw new WBIOException("Cannot add new record", e);
		}
	}
	
	public<T> T update(T t) throws WBIOException
	{
		try
		{
			log.log(Level.INFO, "update record for class %s", t.getClass().getSimpleName());
			localDataStorageDao.updateRecord(t, KEY_FILED_NAME);
			notifyOperation(t, AdminDataStorageOperation.UPDATE);			
			return t;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot add new record", e);
		}
	}
	
	public<T> List<T> query(Class dataClass, String property, AdminQueryOperator operator, Object parameter) throws WBIOException
	{
		try
		{
			Set<String> properties = new HashSet();
			properties.add(property);
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			operators.put(property, adminOperatorToLocalOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			List<T> result = (List<T>)localDataStorageDao.query(dataClass, properties, operators, values);
			return result;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot get all records with sorting", e);
		}

	}
	
	public<T> List<T> queryEx(Class dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values) throws WBIOException
	{
		try
		{
			Map<String, WBLocalQueryOperator> localOperators = new HashMap<String, WBLocalQueryOperator>();
			for(String property: propertyNames)
			{
				localOperators.put(property, adminOperatorToLocalOperator(operators.get(property)));
			}
			List<T> result = (List<T>)localDataStorageDao.query(dataClass, propertyNames, localOperators, values);
			return result;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot get all records with sorting", e);
		}
	}

	public<T> List<T> queryWithSort(Class dataClass, String property, AdminQueryOperator operator, Object parameter, String sortProperty, AdminSortOperator sortOperator) throws WBIOException
	{
		try
		{
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			operators.put(property, adminOperatorToLocalOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			List<T> result = (List<T>)localDataStorageDao.queryWithSort(dataClass, properties, operators, values, sortProperty, adminDirectionToLocalDirection(sortOperator));
			return result;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot get all records with sorting", e);
		}

	}
	
	public<T> List<T> queryExWithSort(Class dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values, String sortProperty, AdminSortOperator sortOperator) throws WBIOException
	{
		try
		{
			Map<String, WBLocalQueryOperator> localOperators = new HashMap<String, WBLocalQueryOperator>();
			for(String property: propertyNames)
			{
				localOperators.put(property, adminOperatorToLocalOperator(operators.get(property)));
			}
			List<T> result = (List<T>)localDataStorageDao.queryWithSort(dataClass, propertyNames, localOperators, values, sortProperty, adminDirectionToLocalDirection(sortOperator));
			return result;
		} catch (Exception e)
		{
			throw new WBIOException("Cannot get all records with sorting", e);
		}
	}	
	

	public void addStorageListener(AdminDataStorageListener listener)
	{
		synchronized (storageListeners)
		{
			storageListeners.add(listener);
		}
	}
	
	public void removeStorageListener(AdminDataStorageListener listener)
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
	
	protected<T> void notifyOperation(Object obj, AdminDataStorageListener.AdminDataStorageOperation operation)
	{
		synchronized (storageListeners)
		{
			for(int i=0; i< storageListeners.size(); i++)
			{
				storageListeners.get(i).notify(obj, operation);
			}
		}		
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
