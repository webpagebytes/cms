
package com.webbricks.utility;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


public class XMLConfigReader {

	public WBConfiguration readSax(InputStream is) throws Exception 
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
