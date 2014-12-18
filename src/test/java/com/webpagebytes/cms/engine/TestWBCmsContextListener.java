package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.WPBCmsContextListener;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({WPBCmsContextListener.class,CmsConfigurationFactory.class})
public class TestWBCmsContextListener {

private Logger loggerMock;
private WPBCmsContextListener contextListener;

@Before
public void setUp()
{
	loggerMock = EasyMock.createMock(Logger.class);
	Whitebox.setInternalState(WPBCmsContextListener.class, "log", loggerMock);
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", (String)null);
	contextListener = new WPBCmsContextListener();
}

@Test
public void test_contextInitialized()
{
	loggerMock.log(EasyMock.anyObject(Level.class), EasyMock.anyObject(String.class));
	ServletContextEvent contextEventMock = EasyMock.createMock(ServletContextEvent.class);
	ServletContext contextMock = EasyMock.createMock(ServletContext.class);
	EasyMock.expect(contextEventMock.getServletContext()).andReturn(contextMock);
	String configPath = "a config path";
	EasyMock.expect(contextMock.getInitParameter(WPBCmsContextListener.CMS_CONFIG_KEY)).andReturn(configPath);
	EasyMock.replay(loggerMock, contextEventMock, contextMock);
	contextListener.contextInitialized(contextEventMock);
	EasyMock.verify(loggerMock, contextEventMock, contextMock);
	assertTrue(CmsConfigurationFactory.getConfigPath().equals(configPath));
	
}

@Test
public void test_contextInitialized_alreadyConfigured()
{
	loggerMock.log(EasyMock.anyObject(Level.class), EasyMock.anyObject(String.class));
	ServletContextEvent contextEventMock = EasyMock.createMock(ServletContextEvent.class);
	ServletContext contextMock = EasyMock.createMock(ServletContext.class);
	EasyMock.expect(contextEventMock.getServletContext()).andReturn(contextMock);
	String configPath = "a config path";
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", (String)configPath);
	EasyMock.expect(contextMock.getInitParameter(WPBCmsContextListener.CMS_CONFIG_KEY)).andReturn(configPath);
	EasyMock.replay(loggerMock, contextEventMock, contextMock);
	contextListener.contextInitialized(contextEventMock);
	EasyMock.verify(loggerMock, contextEventMock, contextMock);
	
}
@Test
public void test_contextInitialized_exception()
{
	loggerMock.log(EasyMock.anyObject(Level.class), EasyMock.anyObject(String.class));
	ServletContextEvent contextEventMock = EasyMock.createMock(ServletContextEvent.class);
	ServletContext contextMock = EasyMock.createMock(ServletContext.class);
	EasyMock.expect(contextEventMock.getServletContext()).andReturn(contextMock);
	String configPath = null;
	EasyMock.expect(contextMock.getInitParameter(WPBCmsContextListener.CMS_CONFIG_KEY)).andReturn(configPath);
	EasyMock.replay(loggerMock, contextEventMock, contextMock);
	try
	{
		contextListener.contextInitialized(contextEventMock);
		assertTrue(false);
	} catch (RuntimeException e)
	{
		EasyMock.verify(loggerMock, contextEventMock, contextMock);	
	}
	
}
@Test
public void test_contextDestroyed()
{
	
	contextListener.contextDestroyed(null);
}
}
