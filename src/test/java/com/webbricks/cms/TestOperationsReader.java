package com.webbricks.cms;

import com.webbricks.cms.*;

import com.webbricks.exception.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
public class TestOperationsReader {

	@Test
	public void testConfigNotFound()
	{
		try
		{
			OperationsReader operationsReader = new OperationsReader();
			operationsReader.initialize("dummyfile");
			assertTrue(false);
		} 
		catch (WBFileNotFoundException e)
		{
			return;
		}
		catch (WBException e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWildOperation()
	{
		try
		{
			OperationsReader operationsReader = new OperationsReader();
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
			OperationsReader operationsReader = new OperationsReader();
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
		catch (WBException e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWrongOperation()
	{
		try
		{
			OperationsReader operationsReader = new OperationsReader();
			operationsReader.initialize("META-INF/config/ajaxwhitelist.properties");
			assertTrue(operationsReader.operationToMethod("/login", "POST") == null);
		} 
		catch (WBException e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testConfigWrongNoController()
	{
		try
		{
			OperationsReader operationsReader = new OperationsReader();
			operationsReader.initialize("META-INF/config/ajaxwhitelist2.properties");
			assertTrue(operationsReader.operationToMethod("/resource/{id}", "POST") == null);
		} 
		catch (WBException e)
		{
			assertTrue(false);
		}		
	}

}
