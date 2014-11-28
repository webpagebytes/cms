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

package com.webpagebytes.cms.datautility;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JSONToFromObjectConverter {

	private String STRING_CLASS_NAME = "java.lang.String";
	private String LONG_CLASS_NAME = "java.lang.Long";
	private String INTEGER_CLASS_NAME = "java.lang.Integer";
	private String DATE_CLASS_NAME = "java.util.Date";
	private String INT_CLASS_NAME = "int";
	private String LNG_CLASS_NAME = "long";
	
	private String JSONStringFromField(Object fieldValue, Class fieldClass)
	{
		if (fieldClass.getName().compareTo(STRING_CLASS_NAME) == 0)
		{
			return fieldValue.toString();
		} else
		if (fieldClass.getName().compareTo(LONG_CLASS_NAME) == 0)
		{
			return fieldValue.toString();
		} else
		if (fieldClass.getName().compareTo(LNG_CLASS_NAME) == 0)
		{
			return fieldValue.toString();
		} else	
		if (fieldClass.getName().compareTo(INTEGER_CLASS_NAME) == 0)
		{
			return fieldValue.toString();
		} else
		if (fieldClass.getName().compareTo(INT_CLASS_NAME) == 0)
		{
			return fieldValue.toString();
		} else
		if (fieldClass.getName().compareTo(DATE_CLASS_NAME) == 0)
		{
			return Long.toString(((Date)fieldValue).getTime());
		}		
		return null;
	}
	
	private Object fieldFromJSON(org.json.JSONObject json, String fieldName, Class fieldClass)
	{
		try
		{
			if (fieldClass.getName().compareTo(STRING_CLASS_NAME) == 0)
			{
				return json.getString(fieldName);
			} else
			if (fieldClass.getName().compareTo(LONG_CLASS_NAME) == 0)
			{
				return json.getLong(fieldName);
			} else
			if (fieldClass.getName().compareTo(LNG_CLASS_NAME) == 0)
			{
				return json.getLong(fieldName);
			} else	
			if (fieldClass.getName().compareTo(INTEGER_CLASS_NAME) == 0)
			{
				return json.getInt(fieldName);
			} else
			if (fieldClass.getName().compareTo(INT_CLASS_NAME) == 0)
			{
				return json.getInt(fieldName);
			} else
			if (fieldClass.getName().compareTo(DATE_CLASS_NAME) == 0)
			{
				return new Date (json.getLong(fieldName));
			}	
		} catch (org.json.JSONException e)
		{
			return null;
		}
		return null;
	}
	
	public Object objectFromJSONString(String jsonString, Class objClass)
	{
		try
		{
			org.json.JSONObject json = new org.json.JSONObject(jsonString);
			Object newObj = objClass.newInstance();
			Field[] fields = objClass.getDeclaredFields();
			for(Field field: fields)
			{
				Object storeAdn = field.getAnnotation(WPBAdminFieldStore.class);
				if (storeAdn == null)
				{
					storeAdn = field.getAnnotation(WPBAdminFieldKey.class);
					if (storeAdn == null)
					{
						storeAdn = field.getAnnotation(WPBAdminFieldTextStore.class);
						if (storeAdn == null)
						{
							storeAdn = field.getAnnotation(WPBAdminField.class);
						}
					}
				}

				if (storeAdn != null)
				{
					String fieldName = field.getName();
					try
					{
						PropertyDescriptor pd = new PropertyDescriptor(fieldName, objClass);
						Object fieldValue = fieldFromJSON(json, fieldName, field.getType());
						if (fieldValue != null)
						{
							pd.getWriteMethod().invoke(newObj, fieldFromJSON(json, fieldName, field.getType()));
						}
					} catch (Exception e)
					{
						// do nothing, there is no write method for our field
					}
				}
			}
			
			return newObj;
			
		} catch (Exception e)
		{
			// do nothing
		} 
		return null;
		
	}
	
	public org.json.JSONObject JSONFromObject(Object object) 
	{
		org.json.JSONObject json = new org.json.JSONObject(); 
		Class objClass = object.getClass();
		Field[] fields = objClass.getDeclaredFields();
		for(Field field: fields)
		{
			Object storeAdn = field.getAnnotation(WPBAdminFieldStore.class);
			if (storeAdn == null)
			{
				storeAdn = field.getAnnotation(WPBAdminFieldKey.class);
				if (storeAdn == null)
				{
					storeAdn = field.getAnnotation(WPBAdminFieldTextStore.class);
					if (storeAdn == null)
					{
						storeAdn = field.getAnnotation(WPBAdminField.class);
					}
				}
			}

			if (storeAdn != null)
			{
				String fieldName = field.getName();
				try
				{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, objClass);
					Object value = pd.getReadMethod().invoke(object);
					String fieldValue = JSONStringFromField(value, field.getType());
					if (fieldValue != null)
					{
						json.put(fieldName, fieldValue);
					}
				} catch (Exception e)
				{
					// do nothing, there is no write method for our field
				}
			}
		}		
		return json;						
	}
	
	public String JSONStringFromObject(Object object, Map<String, String> additionalData)
	{
		org.json.JSONObject json = JSONFromObject(object);
		if (additionalData != null)
		{
			try
			{
				for (String key: additionalData.keySet())
				{
					json.append(key, additionalData.get(key));
				}
			} catch (org.json.JSONException e)
			{
				// TDB
			}
		}
		return json.toString();				
	}
	
	public List listObjectsFromJSONString(String jsonSource, Class objClass)
	{
		List result = new ArrayList();
		try
		{
			org.json.JSONArray jsonArray = new org.json.JSONArray(jsonSource);
			for (int i =0; i< jsonArray.length(); i++)
			{
				org.json.JSONObject objectJson = jsonArray.getJSONObject(i);
				Object obj = objectFromJSONString(objectJson.toString(), objClass);
				result.add(obj);
			}
			
		} catch (org.json.JSONException e)
		{
			return null;
		}
		return result;
	}
	
	public String JSONStringFromListObjects(List listObject)
	{
		org.json.JSONArray jsonArray = new org.json.JSONArray();
		for(Object obj: listObject)
		{
			org.json.JSONObject json = JSONFromObject(obj);
			jsonArray.put(json); 
		}
		return jsonArray.toString();
	}
	
	public org.json.JSONArray JSONArrayFromListObjects(List listObject)
	{
		org.json.JSONArray jsonArray = new org.json.JSONArray();
		for(Object obj: listObject)
		{
			org.json.JSONObject json = JSONFromObject(obj);
			jsonArray.put(json); 
		}
		return jsonArray;
	}
	
	public org.json.JSONObject JSONObjectFromMap(Map<String, Object> map)
	{
		org.json.JSONObject object = new org.json.JSONObject();
		if (map != null)
		{
			for (String key: map.keySet())
			{
				try 
				{
					object.put(key, map.get(key).toString());
				} catch (org.json.JSONException e)
				{
					return null;
				}
			}
		}
		return object;
	}
	
}
