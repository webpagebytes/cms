package com.webpagebytes.utility;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.utility.WBConfiguration;
import com.webpagebytes.utility.XMLConfigReader;
import com.webpagebytes.utility.WBConfiguration.SECTION;

@RunWith(PowerMockRunner.class)
public class TestXMLConfigReader {

	
@Test
public void testSax()
{
	try
	{
	XMLConfigReader reader = new XMLConfigReader();
	InputStream is = this.getClass().getClassLoader().getResourceAsStream("META-INF/wbconfiguration_sax.xml");
	WBConfiguration conf = reader.readConfiguration(is);
	assertTrue(conf != null);
	assertTrue(conf.getSectionClassFactory(WBConfiguration.SECTION.SECTION_CACHE).equals("com.xyz.cache"));
	assertTrue(conf.getSectionClassFactory(WBConfiguration.SECTION.SECTION_FILESTORAGE).equals("com.xyz.abc"));
	assertTrue(conf.getSectionParams(SECTION.SECTION_CACHE) == null);
	assertTrue(conf.getSectionParams(SECTION.SECTION_FILESTORAGE) == null);
	Map<String, String> expectedParams = new HashMap<String, String>();
	expectedParams.put("x", "1");
	expectedParams.put("y", "2");	
	assertTrue(conf.getSectionParams(SECTION.SECTION_DATASTORAGE).equals(expectedParams));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

}
