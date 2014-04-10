package com.webpagebytes.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class WBIOFactory implements IOFactory {

	public Reader createBufferedUTF8Reader(InputStream is)
	{
		try
		{
			return new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e)
		{
			return null;
		}
	}
}
