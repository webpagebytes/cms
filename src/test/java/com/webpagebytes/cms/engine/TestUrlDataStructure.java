package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.engine.URLDataStructure;

@RunWith(PowerMockRunner.class)
public class TestUrlDataStructure {

@Test
public void testURLDataStructure_two()
{
	URLDataStructure urlds = new URLDataStructure("/test/abc");
	ArrayList<String> urlParts = new ArrayList<String>();
	urlParts.add("test");
	urlParts.add("abc");
	assertTrue (urlds.getDeep() == 2);
	assertTrue (urlParts.equals(urlds.getSubUrls()));
}

@Test
public void testURLDataStructure_two_with_index()
{
	URLDataStructure urlds = new URLDataStructure("/test/");
	ArrayList<String> urlParts = new ArrayList<String>();
	urlParts.add("test");
	urlParts.add("");
	assertTrue (urlds.getDeep() == 2);
	assertTrue (urlParts.equals(urlds.getSubUrls()));
}

@Test
public void testURLDataStructure_one()
{
	URLDataStructure urlds = new URLDataStructure("/test");
	ArrayList<String> urlParts = new ArrayList<String>();
	urlParts.add("test");
	assertTrue (urlds.getDeep() == 1);
	assertTrue (urlParts.equals(urlds.getSubUrls()));
}

@Test
public void testURLDataStructure_emptyslash()
{
	URLDataStructure urlds = new URLDataStructure("/");
	ArrayList<String> urlParts = new ArrayList<String>();
	urlParts.add("");
	assertTrue (urlds.getDeep() == 1);
	assertTrue (urlParts.equals(urlds.getSubUrls()));
}

@Test
public void testURLDataStructure_nullUrl()
{
	URLDataStructure urlds = new URLDataStructure(null);
	assertTrue (urlds.getSubUrls() == null);
}

@Test
public void testURLDataStructure_dirtyUrls()
{
	URLDataStructure urlds = new URLDataStructure("/test/aa-{id}/{key}");
	ArrayList<String> urlParts = new ArrayList<String>();
	urlParts.add("test");
	urlParts.add("aa-{id}");
	urlParts.add("{key}");
	assertTrue (urlds.getDeep() == 3);
	assertTrue (urlParts.equals(urlds.getSubUrls()));
	assertTrue (urlds.hasParams() == true);
	Map<Integer, String> cleanParts = urlds.getClearSubUrl();
	Map<Integer, String> dirtyParts = urlds.getDirtySubUrl();
	Map<Integer, String> cleanPartsExpect = new HashMap<Integer, String>();
	Map<Integer, String> dirtyPartsExpect = new HashMap<Integer, String>();
	cleanPartsExpect.put(0, "test");
	dirtyPartsExpect.put(1, "aa-{id}");
	dirtyPartsExpect.put(2, "{key}");
	assertTrue (cleanPartsExpect.equals(cleanParts));
	assertTrue (dirtyPartsExpect.equals(dirtyParts));
	
}

}
