/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.datautility.local;

import java.util.HashMap;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener.AdminDataStorageOperation;
import com.webpagebytes.cms.datautility.local.WPBLocalDataStoreDao.WBLocalQueryOperator;
import com.webpagebytes.cms.datautility.local.WPBLocalDataStoreDao.WBLocalSortDirection;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBLocalAdminDataStorage implements WPBAdminDataStorage {
	private static final Logger log = Logger.getLogger(WPBLocalAdminDataStorage.class.getName());
	private static final String KEY_FILED_NAME = "privkey";
	private Vector<WPBAdminDataStorageListener> storageListeners = new Vector<WPBAdminDataStorageListener>();
	
	private WPBLocalDataStoreDao localDataStorageDao;
	private boolean notificationsFlag = true;
	
	public WPBLocalAdminDataStorage()
	{
		CmsConfiguration config = CmsConfigurationFactory.getConfiguration();
		Map<String, String> params = config.getSectionParams(WPBSECTION.SECTION_DATASTORAGE);
		localDataStorageDao = new WPBLocalDataStoreDao(params);
	}

	private WPBLocalDataStoreDao.WBLocalQueryOperator adminOperatorToLocalOperator(AdminQueryOperator adminOperator)
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
	
	private WPBLocalDataStoreDao.WBLocalSortDirection adminDirectionToLocalDirection(AdminSortOperator sortOperator)
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
	
	public<T> void delete(String recordid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records {0}", recordid);
			localDataStorageDao.deleteRecord(dataClass, KEY_FILED_NAME, recordid);			
			T obj = dataClass.newInstance();
			localDataStorageDao.setObjectProperty(obj, KEY_FILED_NAME, recordid);
			notifyOperation(obj, AdminDataStorageOperation.DELETE_RECORD, dataClass);			

		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete record " + recordid, e);
		}
	}
	
	public<T> void delete(Long recordid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records {0}", recordid);
			localDataStorageDao.deleteRecord(dataClass, KEY_FILED_NAME, recordid);
			T obj = dataClass.newInstance();
			localDataStorageDao.setObjectProperty(obj, KEY_FILED_NAME, recordid);
			notifyOperation(obj, AdminDataStorageOperation.DELETE_RECORD, dataClass);			
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete record " + recordid, e);
		}		
	}
	
	public<T> void delete(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "delete records with property condition {0}", property);		
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			operators.put(property, adminOperatorToLocalOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			localDataStorageDao.deleteRecords(dataClass, properties, operators, values);
			notifyOperation(null, AdminDataStorageOperation.DELETE_RECORDS, dataClass);				
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete records ", e);
		}
	}
	
	public<T> List<T> getAllRecords(Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "get all records {0}", dataClass.getSimpleName());			
			List<T> result = (List<T>) localDataStorageDao.getAllRecords(dataClass);
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("cannot get all records", e);
		} 
	}
	
	public<T> List<T> getAllRecords(Class<T> dataClass, String property, AdminSortOperator sortOperator) throws WPBIOException
	{
		try
		{
			Object [] logObjects = { dataClass.getSimpleName(), property};
			log.log(Level.INFO, "get all records {0} with condition on property {1}", logObjects);			
			
			Set<String> properties = new HashSet<String>();
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			Map<String, Object> values = new HashMap<String, Object>();
			List<T> result = (List<T>)localDataStorageDao.queryWithSort(dataClass, properties, operators, values, property, adminDirectionToLocalDirection(sortOperator));
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot get all records with sorting", e);
		}

	}

	public<T> T add(T t) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "add record for class {0}", t.getClass().getSimpleName());			
			T res = localDataStorageDao.addRecord(t, KEY_FILED_NAME);			
			notifyOperation(t, AdminDataStorageOperation.CREATE_RECORD, t.getClass());			
			return res;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}

	public<T> T addWithKey(T t) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "add record with key for class {0}", t.getClass().getSimpleName());			
			T res = localDataStorageDao.addRecordWithKey(t, KEY_FILED_NAME);
			notifyOperation(t, AdminDataStorageOperation.CREATE_RECORD, t.getClass());			
			return res;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}

	public<T> T get(Long dataid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "get record for key {0}", dataid);			
			return (T) localDataStorageDao.getRecord(dataClass, KEY_FILED_NAME, dataid);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}
	
	public<T> T get(String dataid, Class<T> dataClass) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "get record for key {0}", dataid);
			return (T) localDataStorageDao.getRecord(dataClass, KEY_FILED_NAME, dataid);
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}
	
	public<T> T update(T t) throws WPBIOException
	{
		try
		{
			log.log(Level.INFO, "update record for class {0}", t.getClass().getSimpleName());
			localDataStorageDao.updateRecord(t, KEY_FILED_NAME);
			notifyOperation(t, AdminDataStorageOperation.UPDATE_RECORD, t.getClass());			
			return t;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot add new record", e);
		}
	}
	
	public<T> List<T> query(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException
	{
		try
		{
			Set<String> properties = new HashSet<String>();
			properties.add(property);
			Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
			operators.put(property, adminOperatorToLocalOperator(operator));
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(property, parameter);
			List<T> result = (List<T>)localDataStorageDao.query(dataClass, properties, operators, values);
			return result;
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot get all records with sorting", e);
		}

	}
	
	public<T> List<T> queryEx(Class<T> dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values) throws WPBIOException
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
			throw new WPBIOException("Cannot get all records with sorting", e);
		}
	}

	public<T> List<T> queryWithSort(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter, String sortProperty, AdminSortOperator sortOperator) throws WPBIOException
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
			throw new WPBIOException("Cannot get all records with sorting", e);
		}

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

	
	public String getUploadUrl(String returnUrl)
	{
		return "";
	}
	
	public<T> void deleteAllRecords(Class<T> dataClass) throws WPBIOException
	{
		try
		{
			localDataStorageDao.deleteRecords(dataClass);
			notifyOperation(null, AdminDataStorageOperation.DELETE_RECORDS, dataClass);				
		} catch (Exception e)
		{
			throw new WPBIOException("Cannot delete all records for class records " + dataClass.getSimpleName(), e);
		}
	}
	
	public String getUniqueId()
	{
		return java.util.UUID.randomUUID().toString();
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

}
