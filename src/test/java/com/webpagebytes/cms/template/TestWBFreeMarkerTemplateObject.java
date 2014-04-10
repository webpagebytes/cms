package com.webpagebytes.cms.template;

import java.util.Map;
import java.util.HashMap;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import org.junit.Test;

import com.webpagebytes.cms.template.WBFreeMarkerTemplateObject;
import com.webpagebytes.cms.template.WBFreeMarkerTemplateObject.TemplateType;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerTemplateObject {

@Test
public void testGettersTemplatePage()
{
	WBFreeMarkerTemplateObject object = new WBFreeMarkerTemplateObject("testpage", TemplateType.TEMPLATE_PAGE, 2L);
	assertTrue(object.getName().equals("testpage"));
	assertTrue(object.getType() == TemplateType.TEMPLATE_PAGE);
	assertTrue(object.getLastModified() == 2L);
}

@Test
public void testGettersTemplateModulePage()
{
	WBFreeMarkerTemplateObject object = new WBFreeMarkerTemplateObject("testmodule", TemplateType.TEMPLATE_MODULE, 3L);
	assertTrue(object.getName().equals("testmodule"));
	assertTrue(object.getType() == TemplateType.TEMPLATE_MODULE);
	assertTrue(object.getLastModified() == 3L);
}

@Test
public void testSetters()
{
	WBFreeMarkerTemplateObject object = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 1L);
	object.setName("testset");
	object.setType(TemplateType.TEMPLATE_MODULE);
	object.setLastModified(5L);
	assertTrue(object.getName().equals("testset"));
	assertTrue(object.getType() == TemplateType.TEMPLATE_MODULE);
	assertTrue(object.getLastModified() == 5L);
}

@Test
public void testEquals()
{
	WBFreeMarkerTemplateObject object1 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object2 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object3 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_MODULE, 10L);
	WBFreeMarkerTemplateObject object4 = new WBFreeMarkerTemplateObject("testX", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object5 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 2L);

	assertTrue(object1.equals(object2));
	assertFalse(object1.equals(object3));
	assertFalse(object1.equals(object4));
	assertFalse(object1.equals(object5));
}

@Test
public void testHash()
{
	WBFreeMarkerTemplateObject object1 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object2 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object3 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_MODULE, 10L);
	WBFreeMarkerTemplateObject object4 = new WBFreeMarkerTemplateObject("testX", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object5 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 2L);

	assertTrue(object1.hashCode() == object2.hashCode());
	assertFalse(object1.hashCode() == object3.hashCode());
	assertFalse(object1.hashCode() == object4.hashCode());
	assertFalse(object1.hashCode() == object5.hashCode());
}

@Test
public void testHash_with_map()
{
	WBFreeMarkerTemplateObject object1 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object2 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object3 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_MODULE, 10L);
	WBFreeMarkerTemplateObject object4 = new WBFreeMarkerTemplateObject("testX", TemplateType.TEMPLATE_PAGE, 10L);
	WBFreeMarkerTemplateObject object5 = new WBFreeMarkerTemplateObject("test", TemplateType.TEMPLATE_PAGE, 2L);

	Map<WBFreeMarkerTemplateObject, Integer> map = (Map<WBFreeMarkerTemplateObject, Integer>) new HashMap();
	map.put(object1, 1);
	map.put(object2, 2);
	map.put(object3, 3);
	map.put(object4, 4);
	map.put(object5, 5);
	
	assertTrue(map.size() == 4);
	assertTrue(map.get(object1) == 2);
	assertTrue(map.get(object2) == 2);
	assertTrue(map.get(object5) == 5);
}

@Test
public void testHash_nullname()
{
	WBFreeMarkerTemplateObject object1 = new WBFreeMarkerTemplateObject(null, TemplateType.TEMPLATE_PAGE, 2L);
	WBFreeMarkerTemplateObject object2 = new WBFreeMarkerTemplateObject(null, TemplateType.TEMPLATE_PAGE, 2L);
	assertTrue (object1.hashCode() == object2.hashCode());
}

@Test
public void testHash_equalsWrongType()
{
	WBFreeMarkerTemplateObject object1 = new WBFreeMarkerTemplateObject("abc", TemplateType.TEMPLATE_PAGE, 2L);
	assertTrue (object1.equals("abc") == false);
}

}
