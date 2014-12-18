package com.webpagebytes.cms.engine;


import com.webpagebytes.cms.engine.AdminServletOperationsReader;
import com.webpagebytes.cms.exception.*;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestOperationsReader {

	@Test
	public void testConfigNotFound()
	{
		try
		{
			AdminServletOperationsReader operationsReader = new AdminServletOperationsReader();
			operationsReader.initialize("dummyfile");
			assertTrue(false);
		} 
		catch (WPBFileNotFoundException e)
		{
			return;
		}
		catch (WPBException e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWildOperation()
	{
		try
		{
			AdminServletOperationsReader operationsReader = new AdminServletOperationsReader();
			operationsReader.initialize("META-INF/config/ajaxwhitelist.properties");
			
			assertTrue(operationsReader.wildOperationToMethod("/export_121201.zip", "GET").compareTo("/export_*.zip") == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
		
	}
	
	@Test
	public void testGetOperationsOK()
	{
		try
		{
			AdminServletOperationsReader operationsReader = new AdminServletOperationsReader();
			operationsReader.initialize("META-INF/config/ajaxwhitelist.properties");
			
			assertTrue(operationsReader.operationToMethod("/test", "POST").getFirst().compareTo("Test") == 0);
			assertTrue(operationsReader.operationToMethod("/test", "POST").getSecond().compareTo("test") == 0);

			assertTrue(operationsReader.operationToMethod("/resource/{id}", "POST").getFirst().compareTo("Test") == 0);
			assertTrue(operationsReader.operationToMethod("/resource/{id}", "POST").getSecond().compareTo("putResource") == 0);

			assertTrue(operationsReader.operationToMethod("/resource", "PUT").getFirst().compareTo("Test") == 0);
			assertTrue(operationsReader.operationToMethod("/resource", "PUT").getSecond().compareTo("addResource") == 0);

			assertTrue(operationsReader.operationToMethod("/resources", "GET").getFirst().compareTo("Test") == 0);
			assertTrue(operationsReader.operationToMethod("/resources", "GET").getSecond().compareTo("getResources") == 0);

			assertTrue(operationsReader.operationToMethod("/resource/{id}", "GET").getFirst().compareTo("Test") == 0);
			assertTrue(operationsReader.operationToMethod("/resource/{id}", "GET").getSecond().compareTo("getResource") == 0);

			assertTrue(operationsReader.operationToMethod("/resource/{id}", "DELETE").getFirst().compareTo("Controller") == 0);
			assertTrue(operationsReader.operationToMethod("/resource/{id}", "DELETE").getSecond().compareTo("deleteResource") == 0);

			assertTrue(operationsReader.operationToMethod("/resources", "DELETE").getFirst().compareTo("Controller") == 0);
			assertTrue(operationsReader.operationToMethod("/resources", "DELETE").getSecond().compareTo("deleteResources") == 0);

		} 
		catch (WPBException e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWrongOperation()
	{
		try
		{
			AdminServletOperationsReader operationsReader = new AdminServletOperationsReader();
			operationsReader.initialize("META-INF/config/ajaxwhitelist.properties");
			assertTrue(operationsReader.operationToMethod("/login", "POST") == null);
		} 
		catch (WPBException e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testConfigWrongNoController()
	{
		try
		{
			AdminServletOperationsReader operationsReader = new AdminServletOperationsReader();
			operationsReader.initialize("META-INF/config/ajaxwhitelist2.properties");
			assertTrue(operationsReader.operationToMethod("/resource/{id}", "POST") == null);
		} 
		catch (WPBException e)
		{
			assertTrue(false);
		}		
	}

}
