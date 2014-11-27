package com.webpagebytes.cms.utility;

import static org.junit.Assert.*;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.XMLConfigReader;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

@RunWith(PowerMockRunner.class)
public class TestXMLConfigReader {

	
@Test
public void testSax()
{
	try
	{
	XMLConfigReader reader = new XMLConfigReader();
	InputStream is = this.getClass().getClassLoader().getResourceAsStream("META-INF/wbconfiguration_sax.xml");
	CmsConfiguration conf = reader.readConfiguration(is);
	assertTrue(conf != null);
	assertTrue(conf.getSectionClassFactory(CmsConfiguration.WPBSECTION.SECTION_CACHE).equals("com.xyz.cache"));
	assertTrue(conf.getSectionClassFactory(CmsConfiguration.WPBSECTION.SECTION_FILESTORAGE).equals("com.xyz.abc"));
	assertTrue(conf.getSectionParams(WPBSECTION.SECTION_CACHE) == null);
	assertTrue(conf.getSectionParams(WPBSECTION.SECTION_FILESTORAGE) == null);
	assertTrue(conf.getSectionParams(WPBSECTION.SECTION_MODEL_CONFIGURATOR).size() == 1);
	Map<String, String> expectedParams = new HashMap<String, String>();
	expectedParams.put("x", "1");
	expectedParams.put("y", "2");	
	assertTrue(conf.getSectionParams(WPBSECTION.SECTION_DATASTORAGE).equals(expectedParams));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

}
