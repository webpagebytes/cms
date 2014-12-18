package com.webpagebytes.cms.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletOutputStream;

public class CacheServletOutputStream extends ServletOutputStream
implements Serializable
{
private ServletOutputStream sos;

private ByteArrayOutputStream cache;

public CacheServletOutputStream( ServletOutputStream sos_ )
{
  super( );
  sos = sos_;
  cache = new ByteArrayOutputStream( );
}

public ByteArrayOutputStream getBuffer( )
{
  return cache;
}

public void write(int b)
  throws IOException
{
  sos.write(b);
  cache.write(b);
}

public void write(byte b[])
  throws IOException
{
  sos.write(b);
  cache.write(b);
}

public void write( byte buf[], int offset, int len)
  throws IOException
{
  sos.write(buf, offset, len);
  cache.write(buf, offset, len);
} 
}