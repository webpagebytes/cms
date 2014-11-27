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
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configuration", (Object) null);
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", (Object) null);	
}

@After
public void after()
{
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configuration", (Object) null);
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", (Object) null);
}

@Test
public void test_getConfiguration()
{
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", "META-INF/wbconfiguration.xml");
	CmsConfiguration config1 = CmsConfigurationFactory.getConfiguration();
	assertTrue(config1 != null);
	CmsConfiguration config2 = CmsConfigurationFactory.getConfiguration();	
	assertTrue (config1 == config2);
	
	assertTrue(config1.getSectionClassFactory(CmsConfiguration.WPBSECTION.SECTION_CACHE).equals("com.xyz.cache"));
}

@Test
public void test_no_active_Configuration()
{
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", "META-INF/wbconfiguration_noactive.xml");
	CmsConfiguration config1 = CmsConfigurationFactory.getConfiguration();
	assertTrue(config1 != null);
	assertTrue(config1.getSectionClassFactory(CmsConfiguration.WPBSECTION.SECTION_CACHE) == null);

}

@Test
public void test_getConfiguration_no_config()
{
	Logger loggerMock = EasyMock.createMock(Logger.class);
	Whitebox.setInternalState(CmsConfigurationFactory.class, "configPath", "META-INF/wbconfiguration2.xml");
	Whitebox.setInternalState(CmsConfigurationFactory.class, "log", loggerMock);
	loggerMock.log(EasyMock.anyObject(Level.class), EasyMock.anyObject(String.class), EasyMock.anyObject(Exception.class));
	EasyMock.replay(loggerMock);
	CmsConfiguration config1 = CmsConfigurationFactory.getConfiguration();
	assertTrue(config1 == null);
}



}

