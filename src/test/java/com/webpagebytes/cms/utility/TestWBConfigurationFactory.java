package com.webpagebytes.cms.utility;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class TestWBConfigurationFactory {

@After
public void after()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "CONFIG_FILE_XML", "wbconfiguration.xml");
	Whitebox.setInternalState(WBConfigurationFactory.class, "instance", (Object)null);
	Whitebox.setInternalState(WBConfigurationFactory.class, "configuration", (Object) null);
}

@Test
public void test_getConfiguration()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "CONFIG_FILE_XML", "META-INF/wbconfiguration.xml");
	WBConfiguration config1 = WBConfigurationFactory.getConfiguration();
	assertTrue(config1 != null);
	WBConfiguration config2 = WBConfigurationFactory.getConfiguration();	
	assertTrue (config1 == config2);
	
	assertTrue(config1.getSectionClassFactory(WBConfiguration.SECTION.SECTION_CACHE).equals("com.xyz.cache"));
}

@Test
public void test_no_active_Configuration()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "CONFIG_FILE_XML", "META-INF/wbconfiguration_noactive.xml");
	WBConfiguration config1 = WBConfigurationFactory.getConfiguration();
	assertTrue(config1 != null);
	assertTrue(config1.getSectionClassFactory(WBConfiguration.SECTION.SECTION_CACHE) == null);

}

@Test
public void test_getConfiguration_no_config()
{
	Whitebox.setInternalState(WBConfigurationFactory.class, "CONFIG_FILE_XML", "META-INF/wbconfiguration2.xml");
	WBConfiguration config1 = WBConfigurationFactory.getConfiguration();
	assertTrue(config1 == null);
}



}

