
package com.webpagebytes.utility;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


public class XMLConfigReader {

	public WBConfiguration readConfiguration(InputStream is) throws Exception 
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		XMLConfigContentHandler contentHandler = new XMLConfigContentHandler();
		xmlReader.setContentHandler(contentHandler);
		xmlReader.parse(new InputSource(is));
		return contentHandler.getConfiguration();
	}
}
