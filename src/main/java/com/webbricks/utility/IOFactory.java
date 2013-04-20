package com.webbricks.utility;

import java.io.InputStream;
import java.io.Reader;

public interface IOFactory {

	public abstract Reader createBufferedUTF8Reader(InputStream is);
	
}
