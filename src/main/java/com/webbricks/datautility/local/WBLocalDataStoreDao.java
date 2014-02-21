package com.webbricks.datautility.local;

import java.beans.PropertyDescriptor;

import java.lang.reflect.Field;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.h2.jdbcx.JdbcConnectionPool;

import com.webbricks.cmsdata.WBUri;
import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;
import com.webbricks.datautility.AdminFieldTextStore;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBSerializerException;

public class WBLocalDataStoreDao {
	
	private String dbPath = null;
	private JdbcConnectionPool connectionPool = null;
	
	private static final String QUERY_RECORD = "SELECT * FROM %s WHERE key = ?";
	private static final String QUERY_ALL_RECORDS = "SELECT * FROM %s";
	private static final String QUERY_FOR_INSERT = "SELECT * FROM %s LIMIT 0";
	
	
	public WBLocalDataStoreDao(String dbPath)
	{
		this.dbPath = dbPath;
	}
	private synchronized Connection getConnection() throws SQLException
	{
		if (connectionPool == null)
		{
			connectionPool = JdbcConnectionPool.create("jdbc:h2:" + dbPath, "", "");
		}
    	return connectionPool.getConnection(); 	
	}
	
	private void setObjectProperty(Object object, String property, Object propertyValue) throws WBSerializerException
	{
		try
		{
			PropertyDescriptor pd = new PropertyDescriptor(property, object.getClass());
			pd.getWriteMethod().invoke(object, propertyValue);
		} catch (Exception e)
		{
			throw new WBSerializerException("Cannot set property for object", e);
		}

	}
	
	private Object copyResultSetToObject(ResultSet resultSet, Class kind) throws SQLException, WBSerializerException
	{
		try
		{
			Object result = kind.newInstance();
			Field[] fields = kind.getDeclaredFields();
			for(Field field: fields)
			{
				field.setAccessible(true);
				boolean storeField = (field.getAnnotation(AdminFieldKey.class) != null) || 
									 (field.getAnnotation(AdminFieldStore.class) != null) ||
									 (field.getAnnotation(AdminFieldTextStore.class) != null);
				if (storeField)
				{
					String fieldName = field.getName();
					String fieldNameUpperCase = field.getName().toUpperCase();
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, kind);
						// get the field type
					if (field.getType() == Long.class)
					{
						Long value = resultSet.getLong(fieldNameUpperCase);
						pd.getWriteMethod().invoke(result, value);
					} else if (field.getType() == String.class)							
					{
						String value = resultSet.getString(fieldNameUpperCase);
						pd.getWriteMethod().invoke(result, value);							 
					} else if (field.getType() == Integer.class)							
					{
						Integer value = resultSet.getInt(fieldNameUpperCase);
						pd.getWriteMethod().invoke(result, value);							 
					}
				 }
				}
			return result;
		} catch (Exception e)
		{
			throw new WBSerializerException("Cannot deserialize from Result Set", e);
		}
	}
	
	public void copyObjectToResultSet(Object obj, ResultSet resultSet, Connection connection) throws SQLException, WBSerializerException
	{
		try
		{
			Class kind = obj.getClass();
			Field[] fields = kind.getDeclaredFields();
			for(Field field: fields)
			{
				field.setAccessible(true);
				boolean storeField = (field.getAnnotation(AdminFieldKey.class) != null) || 
									 (field.getAnnotation(AdminFieldStore.class) != null) ||
									 (field.getAnnotation(AdminFieldTextStore.class) != null);
				if (storeField)
				{
					String fieldName = field.getName();
					String fieldNameUpperCase = field.getName().toUpperCase();
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, kind);
					Object value = pd.getReadMethod().invoke(obj);
					if (value == null) continue;
						// get the field type
					if (field.getType() == Long.class)
					{
						Long valueLong = (Long) value;
						resultSet.updateLong(fieldNameUpperCase, valueLong);
					} else if (field.getType() == String.class)							
					{
						String valueString = (String)value;
						if (field.getAnnotation(AdminFieldStore.class) != null)
						{
							resultSet.updateString(fieldNameUpperCase, valueString);
						} else if (field.getAnnotation(AdminFieldTextStore.class) != null)
						{
							Clob clob = connection.createClob();
							clob.setString(0, valueString);
							resultSet.updateClob(fieldNameUpperCase, clob);
						} 
						
					} else if (field.getType() == Integer.class)							
					{
						Integer valueInt = (Integer) value;
						resultSet.updateInt(fieldNameUpperCase, valueInt);
					}  else if (field.getType() == Date.class)							
					{
						Date date = (Date) value;
						java.sql.Date sqlDate = new java.sql.Date(date.getTime());
						resultSet.updateDate(fieldNameUpperCase, sqlDate);
					}
				 }
				}
		} catch (Exception e)
		{
			throw new WBSerializerException("Cannot deserialize from Result Set", e);
		}
	}
	public Object getRecord(Long id, Class kind) throws SQLException, WBException
	{
		Connection con = getConnection();
		try
		{
			String table = kind.getSimpleName().toUpperCase();
			PreparedStatement statement = con.prepareStatement(String.format(QUERY_RECORD, table));
			statement.setLong(1, id);
			
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next())
			{	
				Object obj = copyResultSetToObject(resultSet, kind);
				resultSet.close();
				return obj;
			} else
			{
				resultSet.close();
				return null;
			}
		} catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			con.close();
		}
	}
	
	public Object getAllRecords(Class kind) throws SQLException, WBException
	{
		Connection con = getConnection();
		List<Object> objects = new ArrayList<Object>();
		try
		{
			String table = kind.getSimpleName().toUpperCase();
			PreparedStatement statement = con.prepareStatement(String.format(QUERY_ALL_RECORDS, table));
			
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next())
			{	
				Object obj = copyResultSetToObject(resultSet, kind);
				objects.add(obj);				
			} 
			resultSet.close();
		} catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			con.close();
		}
		return objects;
	}
	
	public<T> T addRecord(T object, String keyProperty) throws SQLException, WBSerializerException
	{
		Connection con = getConnection();
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		String table = object.getClass().getSimpleName().toUpperCase();
		
		try
		{
			con.setAutoCommit(false);
			statement = con.prepareStatement(String.format(QUERY_FOR_INSERT, table), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			resultSet = statement.executeQuery();
			resultSet.moveToInsertRow();
			copyObjectToResultSet(object, resultSet, con);
			resultSet.insertRow();
			con.commit();
			resultSet.moveToCurrentRow();
			Long key = resultSet.getLong("KEY");
			try
			{
				/*
				if (generatedResultSet.next())
				{
					Long key = generatedResultSet.getLong(1);
					setObjectProperty(object, keyProperty, key);
				} else
				{
					throw new WBSerializerException("Cannot get unique id for the added record");
				}
				*/
			} finally
			{
				//generatedResultSet.close();
			} 
			return object;
		} catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
			if (statement != null)
			{
				statement.close();
			}
			con.close();
		}
	}
	
	
}
