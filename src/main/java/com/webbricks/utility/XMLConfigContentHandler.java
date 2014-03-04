package com.webbricks.utility;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLConfigContentHandler extends DefaultHandler {
	StringBuilder strBuilder = new StringBuilder();
	WBDefaultConfiguration configuration = new WBDefaultConfiguration();
	WBConfiguration.SECTION currentSection;
	String activeConfiguration = "";
	boolean recordData = false; 
	
	public WBConfiguration getConfiguration() 
	{
		return configuration;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes)
              throws SAXException
     {
		strBuilder.setLength(0);
		if (qName.equals("wpbcache")) {
			currentSection = WBConfiguration.SECTION.SECTION_CACHE;
		} else if (qName.equals("wpbadmindatastorage")) {
			currentSection = WBConfiguration.SECTION.SECTION_DATASTORAGE;
		} else if (qName.equals("wpbcloudfilestorage")) {
			currentSection = WBConfiguration.SECTION.SECTION_FILESTORAGE;
		} else if (qName.equals("wpbconfiguration"))
		{
			if (activeConfiguration.length()>0 && attributes.getValue("name")!=null && attributes.getValue("name").equals(activeConfiguration))
			{
				recordData = true;
			}
		}
		
		if (qName.equals("param") && recordData)
		{
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			configuration.addParamToSection(currentSection, name, value);
		}
     }
	 public void endElement(String uri, String localName, String qName)
	 {
		 if (recordData)
		 {
			 if (qName.equals("factoryclass"))
			 {
				 configuration.setSectionClassFactory(currentSection, strBuilder.toString().trim());
			 } 
		 }
		 if (qName.equals("activeconfiguration"))
		 {
			 activeConfiguration = strBuilder.toString().trim();
		 } else if (qName.equals("wpbconfiguration"))
		{
				 recordData = false;
		}
		 strBuilder.setLength(0);
	 }
	 
	 public void characters(char[] ch, int start, int length)
	 {
		 strBuilder.append(ch, start, length);
	 }
}
