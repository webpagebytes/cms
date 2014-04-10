package com.webpagebytes.cms;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.Pair;

@RunWith(PowerMockRunner.class)
public class TestPair {

@Test
public void test_constructor_empty()
{
	Pair<String, Integer> pair = new Pair<String, Integer>();
	assertTrue (pair.getFirst() == null);
	assertTrue (pair.getSecond() == null);
}

@Test
public void test_constructor_args()
{
	Pair<String, Integer> pair = new Pair<String, Integer>("aaa", 123);
	
	assertTrue (pair.getFirst().equals("aaa"));
	assertTrue (pair.getSecond().equals(123));
}

}
