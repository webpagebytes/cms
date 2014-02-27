package com.webbricks.datautility.local;

import java.beans.PropertyDescriptor;



import java.lang.reflect.Field;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.h2.jdbcx.JdbcConnectionPool;

import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;
import com.webbricks.datautility.AdminFieldTextStore;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBSerializerException;

/**
 * WBLocalDataStoreDao is a class that allows to read, write, delete and query records
 * from SQL like databases that run in the same operating system instance as the current
 * web server.
 */
public class WBLocalDataStoreDao {
	
	public enum WBLocalQueryOperator{
		LESS_THAN,
		GREATER_THAN,
		EQUAL,
		NOT_EQUAL,
		LESS_THAN_OR_EQUAL,
		GREATER_THAN_OR_EQUAL		
	};
	public enum WBLocalSortDirection {
		NO_SORT,
		ASCENDING,
		DESCENDING
	};

	private String dbPath = null;
	private JdbcConnectionPool connectionPool = null;
	
	private static final String QUERY_RECORD = "SELECT * FROM %s WHERE %s=?";
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
	
	/*
	 * Given an instance of an object that has a particular property this method will set the object property with the 
	 * provided value. It assumes that the object has the setter method for the specified interface
	 * @param object The object instance on which the property will be set
	 * @param property The property name that will be set. This means that there is a setter public method defined for the 
	 * 				   object instance
	 * @param propertyValue The new value for the property that will be set
	 * @throws WBSerializerException If the object property was not set with success 
	 */
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
	private Object getObjectProperty(Object object, String property) throws WBSerializerException
	{
		try
		{
			PropertyDescriptor pd = new PropertyDescriptor(property, object.getClass());
			return pd.getReadMethod().invoke(object);
		} catch (Exception e)
		{
			throw new WBSerializerException("Cannot set property for object", e);
		}
	}
	private boolean hasClassProperty(Class kind, String property) throws WBSerializerException
	{
		try
		{
			PropertyDescriptor pd = new PropertyDescriptor(property, kind);
			return pd.getReadMethod() != null;
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
					} else if (field.getType() == Date.class)
					{
						Timestamp ts = resultSet.getTimestamp(fieldNameUpperCase);
						Date value = new Date(ts.getTime());
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
	
	private String getSQLStringForInsert(Object obj, Set<String> fieldsToIgnore)
	{
		String sqlTemplate = "INSERT INTO %s (%s) values (%s)";
		Class kind = obj.getClass();
		String tableName = kind.getSimpleName();
		String listColumns = "";
		String listParams = "";
		
		Field[] fields = kind.getDeclaredFields();

		for(Field field: fields)
		{
			boolean storeField = (field.getAnnotation(AdminFieldKey.class) != null) || 
								 (field.getAnnotation(AdminFieldStore.class) != null) ||
								 (field.getAnnotation(AdminFieldTextStore.class) != null);
			if (storeField)
			{
				String fieldName = field.getName();
				String fieldNameUpperCase = field.getName().toUpperCase();
				if (fieldsToIgnore!=null && fieldsToIgnore.contains(fieldName))
				{
					continue;
				}
				listColumns = listColumns.concat(fieldNameUpperCase).concat(",");
				listParams = listParams.concat("?,");
			}
		} 
		if (listColumns.endsWith(","))
		{
			listColumns = listColumns.substring(0, listColumns.length()-1);
		}
		if (listParams.endsWith(","))
		{
			listParams = listParams.substring(0, listParams.length()-1);
		}

		return String.format(sqlTemplate, tableName, listColumns, listParams) ;
	}

	private String getSQLStringForUpdate(Object object, String keyFieldName) throws WBSerializerException
	{
		String sqlTemplate = "UPDATE %s SET %s WHERE %s=?";
		Class kind = object.getClass();
		String tableName = kind.getSimpleName();
		String listColumns = "";
		
		Field[] fields = kind.getDeclaredFields();
		for(Field field: fields)
		{
			if (field.getAnnotation(AdminFieldKey.class) != null)
			{
				continue;
			}
			boolean storeField = (field.getAnnotation(AdminFieldKey.class) != null) || 
								 (field.getAnnotation(AdminFieldStore.class) != null) ||
								 (field.getAnnotation(AdminFieldTextStore.class) != null);
			if (storeField)
			{
				String fieldNameUpperCase = field.getName().toUpperCase();
				listColumns = listColumns.concat(fieldNameUpperCase).concat("=?,");
			}
		} 
		if (listColumns.endsWith(","))
		{
			listColumns = listColumns.substring(0, listColumns.length()-1);
		}
		return String.format(sqlTemplate, tableName, listColumns, keyFieldName) ;
	}

	private String getSQLStringForDelete(Class kind, String keyFieldName) throws WBSerializerException
	{
		String sqlTemplate = "DELETE FROM %s WHERE %s=?";
		String tableName = kind.getSimpleName();		
		return String.format(sqlTemplate, tableName, keyFieldName) ;
	}

	private int buildStatementForInsertUpdate(Object obj, Set<String> ignoreFields, PreparedStatement preparedStatement, Connection connection) throws SQLException, WBSerializerException
	{
		Class kind = obj.getClass();
		Field[] fields = kind.getDeclaredFields();		
		int fieldIndex = 0;
		for(int i = 0; i< fields.length; i++)
		{
			Field field = fields[i];
			field.setAccessible(true);
			boolean storeField = (field.getAnnotation(AdminFieldKey.class) != null) || 
								 (field.getAnnotation(AdminFieldStore.class) != null) ||
								 (field.getAnnotation(AdminFieldTextStore.class) != null);
			if (storeField)
			{
				String fieldName = field.getName();
				if (ignoreFields!= null && ignoreFields.contains(fieldName))
				{
					continue;
				}
				fieldIndex = fieldIndex + 1;
				Object value = null;
				try
				{
					PropertyDescriptor pd = new PropertyDescriptor(fieldName, kind);
					value = pd.getReadMethod().invoke(obj);
				} catch (Exception e)
				{
					throw new WBSerializerException("Cannot get property value", e);
				}
				if (field.getType() == Long.class)
				{
					Long valueLong = (Long) value;
					preparedStatement.setLong(fieldIndex, valueLong);
				} else if (field.getType() == String.class)							
				{
					String valueString = (String)value;
					if (field.getAnnotation(AdminFieldStore.class) != null)
					{
						preparedStatement.setString(fieldIndex, valueString);
					} else if (field.getAnnotation(AdminFieldKey.class) != null)
					{
						preparedStatement.setString(fieldIndex, valueString);
					} else 	if (field.getAnnotation(AdminFieldTextStore.class) != null)
					{
						Clob clob = connection.createClob();
						clob.setString(1, valueString);
						preparedStatement.setClob(fieldIndex, clob);
					} 						
				} else if (field.getType() == Integer.class)							
				{
					Integer valueInt = (Integer) value;
					preparedStatement.setInt(fieldIndex, valueInt);
				}  else if (field.getType() == Date.class)							
				{
					Date date = (Date) value;
					java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
					preparedStatement.setTimestamp(fieldIndex, sqlDate);
				}
			 }
		}
		return fieldIndex;
	}
	
	public Object getRecord(Class kind, String keyFieldName, Object keyValue) throws SQLException, WBException
	{
		Connection con = getConnection();
		ResultSet resultSet = null;
		try
		{
			String table = kind.getSimpleName().toUpperCase();
			PreparedStatement statement = con.prepareStatement(String.format(QUERY_RECORD, table, keyFieldName));
			setPrepareStatementParameter(statement, 1, keyValue);
			
			resultSet = statement.executeQuery();
			if (resultSet.next())
			{	
				Object obj = copyResultSetToObject(resultSet, kind);
				return obj;
			} else
			{
				return null;
			}
		} catch (SQLException e)
		{
			throw e;
		}
		finally
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
			con.close();
		}
	}
	
	private String operatorToString(WBLocalQueryOperator operator)
	{
		String operation = "";
		switch (operator)
		{
			case EQUAL:
				operation = "=";
				break;
			case NOT_EQUAL:
				operation = "!=";
				break;
			case GREATER_THAN:
				operation = ">";
				break;
			case LESS_THAN:
				operation = "<";
				break;
			case GREATER_THAN_OR_EQUAL:
				operation= ">=";
				break;
			case LESS_THAN_OR_EQUAL:
				operation = "<=";
				break;

		}
		return operation;
	}
	
	private String sortDirectionToString(WBLocalSortDirection sortDir)
	{
		String dir = "";
		switch (sortDir)
		{
			case ASCENDING:
				dir = "ASC";
				break;
			case DESCENDING:
				dir = "DESC";
				break;
			case NO_SORT:
				dir = "";
		}
		return dir;
	}

	
	private List<Object> advanceQuery(Class kind, Set<String> propertyNames, Map<String, WBLocalQueryOperator> operators, Map<String, Object> values, String sortProperty, WBLocalSortDirection sortDirection) throws SQLException, WBSerializerException
	{
		List<String> propertiesList = new ArrayList<String>();
		propertiesList.addAll(propertyNames);
		
		String condition = "";
		for (String property: propertiesList)
		{
			if (! hasClassProperty(kind, property))
			{
				throw new SQLException("queryProperty value does not match a field of given Class kind");
			}
			if (condition.length()>0)
			{
				condition = condition.concat("AND");
			}
			condition = condition.concat("(").concat(property).concat(operatorToString(operators.get(property))).concat("?").concat(")");
		}
		String sort = "";
		if (sortProperty != null)
		{
			if (! hasClassProperty(kind, sortProperty))
			{
				throw new SQLException("sortProperty value does not match a field of given Class kind");
			}
			if (sortDirection != null && sortDirection != WBLocalSortDirection.NO_SORT)
			{
				sort = String.format("ORDER BY %s %s", sortProperty, sortDirectionToString(sortDirection));
			}

		}
		String tableName = kind.getSimpleName();
		String queryString = "";
		if (condition.length()>0)
		{
			queryString = String.format("SELECT * FROM %s WHERE %s %s", tableName, condition, sort);
		} else
		{
			queryString = String.format("SELECT * FROM %s %s", tableName, sort);
		}
		
		Connection connection = getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			List<Object> records = new ArrayList<Object>();
			preparedStatement = connection.prepareStatement(queryString);
			
			for (int i = 0; i< propertiesList.size(); i++)
			{
				setPrepareStatementParameter(preparedStatement, i+1, values.get(propertiesList.get(i)));
			}
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next())
			{	
				Object obj = copyResultSetToObject(resultSet, kind);
				records.add(obj);				
			} 
			resultSet.close();
			return records;

		} catch (Exception e)
		{
			throw e;
		}
		finally 
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			if (connection!=null)
			{
				connection.close();
			}
		}
	}
	
	private boolean advanceDelete(Class kind, Set<String> propertyNames, Map<String, WBLocalQueryOperator> operators, Map<String, Object> values) throws SQLException, WBSerializerException
	{
		List<String> propertiesList = new ArrayList<String>();
		propertiesList.addAll(propertyNames);
		
		String condition = "";
		for (String property: propertiesList)
		{
			if (! hasClassProperty(kind, property))
			{
				throw new SQLException("queryProperty value does not match a field of given Class kind");
			}
			if (condition.length()>0)
			{
				condition = condition.concat("AND");
			}
			condition = condition.concat("(").concat(property).concat(operatorToString(operators.get(property))).concat("?").concat(")");
		}
		String tableName = kind.getSimpleName();
		String queryString = String.format("DELETE FROM %s WHERE %s", tableName, condition);
		
		Connection connection = getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			preparedStatement = connection.prepareStatement(queryString);
			
			for (int i = 0; i< propertiesList.size(); i++)
			{
				setPrepareStatementParameter(preparedStatement, i+1, values.get(propertiesList.get(i)));
			}
			return preparedStatement.execute();

		} catch (Exception e)
		{
			throw e;
		}
		finally 
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			if (connection!=null)
			{
				connection.close();
			}
		}
	}

	public List<Object> query(Class kind, Set<String> propertyNames, Map<String, WBLocalQueryOperator> operators, Map<String, Object> values) throws SQLException, WBSerializerException
	{
		return advanceQuery(kind, propertyNames, operators, values, null, WBLocalSortDirection.NO_SORT);
	}

	public boolean deleteRecords(Class kind, Set<String> propertyNames, Map<String, WBLocalQueryOperator> operators, Map<String, Object> values) throws SQLException, WBSerializerException
	{
		return advanceDelete(kind, propertyNames, operators, values);
	}

	public List<Object> queryWithSort(Class kind, Set<String> propertyNames, Map<String, WBLocalQueryOperator> operators, Map<String, Object> values, String sortProperty, WBLocalSortDirection sortDirection) throws SQLException, WBSerializerException
	{
		return advanceQuery(kind, propertyNames, operators, values, sortProperty, sortDirection);
	}

	
	public List<Object> getAllRecords(Class kind) throws SQLException, WBException
	{
		Connection con = getConnection();
		PreparedStatement statement = null;
		List<Object> objects = new ArrayList<Object>();
		try
		{
			String table = kind.getSimpleName().toUpperCase();
			statement = con.prepareStatement(String.format(QUERY_ALL_RECORDS, table));
			
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
			if (statement != null)
			{
				statement.close();
			}
			con.close();
		}
		return objects;
	}
	
	public<T> T addRecordWithKey(T object, String keyFieldName) throws SQLException, WBSerializerException
	{
		Connection connection = getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			Set<String> ignoreFields = new HashSet();
			String sqlStatement = getSQLStringForInsert(object, ignoreFields) ;			
			connection.setAutoCommit(true);
			preparedStatement = connection.prepareStatement(sqlStatement);
			buildStatementForInsertUpdate(object, ignoreFields, preparedStatement, connection);
			preparedStatement.execute();
			return object;
		} catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			connection.close();
		}
	}

	public<T> T addRecord(T object, String keyFieldName) throws SQLException, WBSerializerException
	{
		Connection connection = getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			Set<String> ignoreFields = new HashSet();
			ignoreFields.add(keyFieldName);
			String sqlStatement = getSQLStringForInsert(object, ignoreFields) ;			
			connection.setAutoCommit(true);
			preparedStatement = connection.prepareStatement(sqlStatement);
			buildStatementForInsertUpdate(object, ignoreFields, preparedStatement, connection);
			preparedStatement.execute();
			ResultSet resultKey = preparedStatement.getGeneratedKeys();
			if (resultKey.next())
			{
				Long key = resultKey.getLong(1);
				setObjectProperty(object, keyFieldName, key);
			}			
			return object;
		} catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			connection.close();
		}
	}

	public void updateRecord(Object object, String keyFieldName) throws SQLException, WBSerializerException
	{
		Connection connection = getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			Set<String> ignoreFields = new HashSet<String>();
			ignoreFields.add(keyFieldName);
			
			String sqlStatement = getSQLStringForUpdate(object, keyFieldName) ;			
			connection.setAutoCommit(true);
			preparedStatement = connection.prepareStatement(sqlStatement);
			int fieldsCount = buildStatementForInsertUpdate(object, ignoreFields, preparedStatement, connection);
			Object keyValue = getObjectProperty(object, keyFieldName);
			setPrepareStatementParameter(preparedStatement, fieldsCount+1, keyValue);
			
			preparedStatement.execute();
		} catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			connection.close();
		}
	}

	public void deleteRecord(Class kind, String fieldName, Object keyValue) throws SQLException, WBSerializerException
	{
		Connection connection = getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			String sqlStatement = getSQLStringForDelete(kind, fieldName) ;			
			connection.setAutoCommit(true);
			preparedStatement = connection.prepareStatement(sqlStatement);
			setPrepareStatementParameter(preparedStatement, 1, keyValue);
		
			preparedStatement.execute();
		} catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (preparedStatement != null)
			{
				preparedStatement.close();
			}
			connection.close();
		}
	}

	private void setPrepareStatementParameter(PreparedStatement preparedStatement, int index, Object param) throws SQLException, WBSerializerException
	{
		if (param.getClass().equals(Integer.class))
		{
			preparedStatement.setInt(index, (Integer)param);
		} else if (param.getClass().equals(Long.class))
		{
			preparedStatement.setLong(index, (Long)param);
		} else if (param.getClass().equals(String.class))
		{
			preparedStatement.setString(index, (String)param);
		} else
		{
			throw new WBSerializerException("Unsupported key type");
		}			

	}
	
}
