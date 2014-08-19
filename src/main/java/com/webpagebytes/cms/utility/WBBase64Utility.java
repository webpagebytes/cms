package com.webpagebytes.cms.utility;

import javax.xml.bind.DatatypeConverter;

public class WBBase64Utility {
	public static String toBase64(byte[] value)
	{
		return DatatypeConverter.printBase64Binary(value);
	}
	public static byte[] fromBase64(String value)
	{
		return DatatypeConverter.parseBase64Binary(value);
	}
	public static String toSafePathBase64(byte[] value)
	{
		// base 64 might contain '/' so we need to replace that with a char not from base 64 set and is path friendly (like '-')
		return toBase64(value).replace('/', '-');
	}
	public static byte[] fromSafePathBase64(String value)
	{
		// base 64 might contain '/' so we need to replace that with a char not from base 64 set and is path friendly (like '-')
		// this is the reverse operation
		return fromBase64(value.replace('-', '/'));
	}
}
