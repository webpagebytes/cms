package com.webpagebytes.cms.utility;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class TestWBConfigurationFactory {

@Before
public void before()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "configuration", (Object) null);
	Whitebox.setInternalState(WBConfigurationFactory.class, "configPath", (Object) null);	
}

@After
public void after()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "configuration", (Object) null);
	Whitebox.setInternalState(WBConfigurationFactory.class, "configPath", (Object) null);
}

@Test
public void test_getConfiguration()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "configPath", "META-INF/wbconfiguration.xml");
	WBConfiguration config1 = WBConfigurationFactory.getConfiguration();
	assertTrue(config1 != null);
	WBConfiguration config2 = WBConfigurationFactory.getConfiguration();	
	assertTrue (config1 == config2);
	
	assertTrue(config1.getSectionClassFactory(WBConfiguration.SECTION.SECTION_CACHE).equals("com.xyz.cache"));
}

@Test
public void test_no_active_Configuration()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "configPath", "META-INF/wbconfiguration_noactive.xml");
	WBConfiguration config1 = WBConfigurationFactory.getConfiguration();
	assertTrue(config1 != null);
	assertTrue(config1.getSectionClassFactory(WBConfiguration.SECTION.SECTION_CACHE) == null);

}

@Test
public void test_getConfiguration_no_config()
{
	Logger loggerMock = EasyMock.createMock(Logger.class);
	Whitebox.setInternalState(WBConfigurationFactory.class, "configPath", "META-INF/wbconfiguration2.xml");
	Whitebox.setInternalState(WBConfigurationFactory.class, "log", loggerMock);
	loggerMock.log(EasyMock.anyObject(Level.class), EasyMock.anyObject(String.class), EasyMock.anyObject(Exception.class));
	EasyMock.replay(loggerMock);
	WBConfiguration config1 = WBConfigurationFactory.getConfiguration();
	assertTrue(config1 == null);
}



}

