/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.utility;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLConfigContentHandler extends DefaultHandler {
	StringBuilder strBuilder = new StringBuilder();
	CmsDefaultConfiguration configuration = new CmsDefaultConfiguration();
	CmsConfiguration.WPBSECTION currentSection;
	
	public CmsConfiguration getConfiguration() 
	{
		return configuration;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes)
              throws SAXException
     {
		strBuilder.setLength(0);
		if (qName.equals("wpbcache")) {
			currentSection = CmsConfiguration.WPBSECTION.SECTION_CACHE;
		} else if (qName.equals("wpbadmindatastorage")) {
			currentSection = CmsConfiguration.WPBSECTION.SECTION_DATASTORAGE;
		} else if (qName.equals("wpbfilestorage")) {
			currentSection = CmsConfiguration.WPBSECTION.SECTION_FILESTORAGE;
		} else if (qName.equals("wpbimageprocessor")) {
			currentSection = CmsConfiguration.WPBSECTION.SECTION_IMAGEPROCESSOR;
		} else if (qName.equals("wpbmodel")) {
			currentSection = CmsConfiguration.WPBSECTION.SECTION_MODEL_CONFIGURATOR;
		} else if (qName.equals("wpbgeneral")) {
            currentSection = CmsConfiguration.WPBSECTION.SECTION_GENERAL;
        } 
		
		if (qName.equals("param"))
		{
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			configuration.addParamToSection(currentSection, name, value);
		}
     }
	 public void endElement(String uri, String localName, String qName)
	 {
		 if (qName.equals("factoryclass"))
		 {
			 configuration.setSectionClassFactory(currentSection, strBuilder.toString().trim());
		 } 
		 strBuilder.setLength(0);
	 }
	 
	 public void characters(char[] ch, int start, int length)
	 {
		 strBuilder.append(ch, start, length);
	 }
}
