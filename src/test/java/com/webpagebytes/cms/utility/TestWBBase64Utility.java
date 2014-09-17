package com.webpagebytes.cms.utility;

import static org.junit.Assert.*;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.utility.WBBase64Utility;

@RunWith(PowerMockRunner.class)
public class TestWBBase64Utility {

@Test 
public void test_fromSafePathBase64()
{
	String x = "\u6700\u7d42\u9078\u8003\u306e\u7d50\u679c\u306f";
	String encode = WBBase64Utility.toSafePathBase64(x.getBytes(Charset.forName("UTF-8")));
	String decode = new String(WBBase64Utility.fromSafePathBase64(encode), Charset.forName("UTF-8")); 
	assertTrue (decode.equals(x));
}
@Test 
public void test_constructor()
{
	WBBase64Utility u = new WBBase64Utility();
	assertTrue(u != null);
}

}
