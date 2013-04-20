package com.webbricks.datautility;

import java.beans.IntrospectionException;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import java.lang.reflect.Method;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.exception.WBSetKeyException;


public class GaeDataStoreUtility {

	private WBGaeDataFactory gaeDataFactory;
	
	public GaeDataStoreUtility()
	{
		gaeDataFactory = new WBDefaultGaeDataFactory();
	}
	public WBGaeDataFactory getGaeDataFactory() {
		return gaeDataFactory;
	}

	public void setGaeDataFactory(WBGaeDataFactory gaeDataFactory) {
		this.gaeDataFactory = gaeDataFactory;
	}

	/*
	 * Given an object with a field annotated with @AdminFieldKey
	 * it will set this field value with key unless a value already exists
	 */
	public void populateObjectWithKey(Object obj, Key key) throws WBException
	{
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field: fields)
		{
			field.setAccessible(true);
			Object storeAdn = field.getAnnotation(AdminFieldKey.class);
			if (storeAdn != null)
			{
				String fieldName = field.getName();
				try
				{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
						// get the field type
						if (field.getType() == Long.class)
						{
							Long value = key.getId();
							pd.getWriteMethod().invoke(obj, value);
							break;
						} else if (field.getType() == String.class)							
						{
							String value = key.getName();
							pd.getWriteMethod().invoke(obj, value);							 
							break;
						}
				} catch (Exception e)
				{
					throw new WBSetKeyException("PopulateObjectWithKey", e);
				}
			}
		}			
	}
	
	/*
	 * Given an object having a field annotated with AdminFieldKey it returns a new
	 * Entity instance with key from the annotated field.  
	 */
	public Entity getEmptyEntityWithKey(Object obj) throws WBException
	{
		String className = obj.getClass().getName();
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field: fields)
		{
			field.setAccessible(true);
			Object storeAdn = field.getAnnotation(AdminFieldKey.class);
			if (storeAdn != null)
			{
				String fieldName = field.getName();
				try
				{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
						// get the field type
					if (field.getType() == Long.class)
					{
						Long value = (Long) pd.getReadMethod().invoke(obj);								
						Entity entity = null;
						if (value != null)
						{
							entity = gaeDataFactory.createEntity(className, value);
						} else
						{
							entity = gaeDataFactory.createEntity(className);
						}
						return entity;
					} else if (field.getType() == String.class)						
					{
						String value = (String) pd.getReadMethod().invoke(obj);
						Entity entity = null;
						if (value != null)
						{
							entity = gaeDataFactory.createEntity(className, value);
						} else
						{
							entity = gaeDataFactory.createEntity(className);
						}
						return entity;
					 } else
					 {
						 throw new WBIOException("Cannot create entity from objects with key differnt than Long or String.");
					 }
				} catch (Exception e)
				{
					throw new WBIOException("getEmptyEntityWithKey", e);
				}
			}
		}
		return null;
			
	}
	
	
	/*
	 * Given an entity and a class it creates a new instance by transferring entity
	 * properties to the new object instance fields annotated with @AdminFieldStore 
	 */
	public Object objectFromEntity(Entity entity, Class dataClass) throws WBIOException
	{
		Map<String, Object> props = entity.getProperties();
		Object newObj = null;
		try
		{
			newObj = dataClass.newInstance();
		} catch (Exception e)
		{
			throw new WBIOException("objectFromEntity", e);
		}
		
		Field[] fields = dataClass.getDeclaredFields();
		for(Field field: fields)
		{
			field.setAccessible(true);
			Object storeAdn = field.getAnnotation(AdminFieldStore.class);
			boolean isText = false;
			if (storeAdn == null)
			{
				storeAdn = field.getAnnotation(AdminFieldTextStore.class);
				if (storeAdn != null)
				{
					isText = true;
				}
			}
			if (storeAdn != null)
			{
				String fieldName = field.getName();
				try
				{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, dataClass);
					Method m = pd.getWriteMethod();
					Class [] params = m.getParameterTypes(); 
					Object o = props.get(fieldName);
					if ((params[0] == Integer.class) && (o != null))
					{
						if (o.getClass() == Integer.class)
						{
							m.invoke(newObj, (Integer)o);
						}
						if (o.getClass() == Long.class)
						{
							m.invoke(newObj, Integer.valueOf(((Long)o).intValue()));
						}
						
					} else 
					{
						if (! isText)
						{
							if (o != null)
							{
								m.invoke(newObj, o);
							}
						} else
						{
							Text text = (Text) o;
							if (text != null)
							{
								m.invoke(newObj, text.getValue());
							} else
							{
								String s = null;
								m.invoke(newObj, s);
							}
						}
					}	
				} catch (Exception e)
				{
					// do nothing, there is no write method for our field
				}
			} else
			{
				Object keyAdn = field.getAnnotation(AdminFieldKey.class);
				if (keyAdn !=null)
				{
					String fieldName = field.getName();
					try
					{
						PropertyDescriptor pd = new PropertyDescriptor(fieldName, dataClass);
						Key key = entity.getKey();
						if (field.getType() == Long.class) 
						{
							pd.getWriteMethod().invoke(newObj, key.getId());
						} 
						if (field.getType() == String.class)
						{
							pd.getWriteMethod().invoke(newObj, key.getName());
						}
					} catch (Exception e)
					{
						// do nothing, there is no write method for our field
					}
				}
				
			}
		}
		return newObj;
	}
	
	/*
	 * Given an object with @AdminFieldStore fields it transfer these fields into
	 * destination Entity instance properties
	 */
	public Entity entityFromObject(Entity destination, Object obj) throws WBIOException
	{
		Field[] fields = obj.getClass().getDeclaredFields();
		for(Field field: fields)
		{
			Object storeAdn = field.getAnnotation(AdminFieldStore.class);
			boolean isText = false;
			if (storeAdn == null)
			{
				storeAdn = field.getAnnotation(AdminFieldTextStore.class);
				if (storeAdn != null)
				{
					isText = true;
				}
			}
			if (storeAdn != null)
			{
				String fieldName = field.getName();
				try
				{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
					Object value = pd.getReadMethod().invoke(obj);
					if (! isText)
					{
						destination.setProperty(fieldName, value);
					} else
					{
						destination.setUnindexedProperty(fieldName, new Text ((String)value));
					}
				} catch (Exception e)
				{
					// do nothing, there is no write method for our field
				}
			}
				
		}
		return destination;
	}
	
	public String getUploadUrl(String returnUrl)
	{
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        return blobstoreService.createUploadUrl(returnUrl);
	}
	

}
