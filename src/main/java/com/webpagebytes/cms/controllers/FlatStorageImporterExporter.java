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

package com.webpagebytes.cms.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.WPBImageProcessor;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBFileInfo;
import com.webpagebytes.cms.cmsdata.WPBExporter;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBImporter;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.engine.DefaultWPBCacheFactory;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.engine.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.engine.WPBImageProcessorFactory;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

public class FlatStorageImporterExporter {
	private static final Logger log = Logger.getLogger(FlatStorageImporterExporter.class.getName());

	public static final String PATH_URIS = "siteuris/";
	public static final String PATH_URI_PARAMETERS = "siteuris/%s/parameters/";
	public static final String PATH_SITE_PAGES = "sitepages/";
	public static final String PATH_SITE_PAGES_PARAMETERS = "sitepages/%s/parameters/";
	public static final String PATH_SITE_PAGES_MODULES = "sitepagesmodules/";
	public static final String PATH_MESSAGES = "messages/";
	public static final String PATH_FILES = "files/";
	public static final String PATH_FILE_CONTENT = "files/%s/content/";
	
	public static final String PATH_ARTICLES = "articles/";
	public static final String PATH_GLOBALS = "settings/globals/";
	public static final String PATH_LOCALES = "settings/locales/";
	
	public static final String PUBLIC_BUCKET = "public";
	
	private WPBExporter exporter = new WPBExporter();
	private WPBImporter importer = new WPBImporter();
	
	private WPBAdminDataStorage dataStorage = WPBAdminDataStorageFactory.getInstance();
	private WPBFileStorage cloudFileStorage = WPBCloudFileStorageFactory.getInstance();
	private WPBImageProcessor imageProcessor = WPBImageProcessorFactory.getInstance();
	
	private UriValidator uriValidator = new UriValidator();
	
	private void exportToXMLFormat(Map<String, Object> props, OutputStream os) throws IOException
	{
		Properties properties = new Properties();
		properties.putAll(props);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		properties.storeToXML(bos, "", "UTF-8");
		byte metadataBytes[] = bos.toByteArray();
		os.write(metadataBytes);
	}
	private Map<Object, Object> importFromXMLFormat(InputStream is) throws IOException
	{
		Properties properties = new Properties();
		byte[] buffer = getBytesFromInputStream(is);
		ByteArrayInputStream byteIs = new ByteArrayInputStream(buffer);
		properties.loadFromXML(byteIs);
		return properties;		
	}
	private void resetCache() throws WPBIOException
	{
	    WPBCacheFactory cacheFactory = DefaultWPBCacheFactory.getInstance();
	    
	    cacheFactory.getWebPagesCacheInstance().Refresh();
	    cacheFactory.getPageModulesCacheInstance().Refresh();
	    cacheFactory.getArticlesCacheInstance().Refresh();
	    cacheFactory.getMessagesCacheInstance().Refresh();
	    cacheFactory.getParametersCacheInstance().Refresh();
	    cacheFactory.getFilesCacheInstance().Refresh();
	    cacheFactory.getProjectCacheInstance().Refresh();
	    cacheFactory.getUrisCacheInstance().Refresh();
        
	}

	public void importFromZip(InputStream is) throws WPBIOException
	{
		ZipInputStream zis = new ZipInputStream(is);
		// we need to stop the notifications during import
		dataStorage.stopNotifications();
		try
		{
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null)
			{
				String name = ze.getName();
				if (name.indexOf(PATH_URIS)>=0)
				{
					if (name.indexOf("/parameters/")>=0 && name.indexOf("metadata.xml")>=0)
					{
						importParameter(zis);
					} else if (name.indexOf("metadata.xml")>=0)
					{
						// this is a web site url
						importUri(zis);
					}
				} else
				if (name.indexOf(PATH_GLOBALS)>=0)
				{
					if (name.indexOf("metadata.xml")>=0)
					{
						importParameter(zis);
					} 
				} else
				if (name.indexOf(PATH_SITE_PAGES)>=0)
				{
					if (name.indexOf("/parameters/")>=0 && name.indexOf("metadata.xml")>=0)
					{
						importParameter(zis);
					} else
					if (name.indexOf("pageSource.txt")>=0)
					{
						importWebPageSource(zis, ze.getName());
					}
					else
					if (name.indexOf("metadata.xml")>=0)
					{
						importWebPage(zis);
					}
				} else
				if (name.indexOf(PATH_SITE_PAGES_MODULES) >= 0)
				{
					if (name.indexOf("moduleSource.txt")>=0)
					{
						importWebPageModuleSource(zis, ze.getName());
					} else
					if (name.indexOf("metadata.xml")>=0)
					{						
						importPageModule(zis);
					}
				} else				
				if (name.indexOf(PATH_ARTICLES) >= 0)
				{
					if (name.indexOf("articleSource.txt")>=0)
					{
						importArticleSource(zis, ze.getName());
					} else
					if (name.indexOf("metadata.xml")>=0)
					{						
						importArticle(zis);
					}
				} else
				if (name.indexOf(PATH_MESSAGES) >= 0)
				{
					importMessage(zis);
				} else
				if (name.indexOf(PATH_FILES) >= 0)
				{
					if (name.indexOf("metadata.xml")>=0)
					{
						importFile(zis);
					} else
					if (name.indexOf("/content/")>=0 && !name.endsWith("/"))
					{
						importFileContent(zis, ze.getName());
					}
				} else
				if (name.indexOf(PATH_LOCALES)>=0)
				{
					importProject(zis);
				}
				zis.closeEntry();
			}
		} catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("cannot import from  zip", e);
		}
		finally
		{
		    dataStorage.startNotifications();
		}
		resetCache();
	}
	
	private byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
		byte buffer[] = new byte[4096];
		ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
		int len = 0;
		while ( (len = is.read(buffer))>0)
		{
			os.write(buffer, 0, len);
		}
		return os.toByteArray();
	}
	
	public void importUri(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBUri uri = importer.buildUri(props);
			if (uri != null)
			{
				if (uriValidator.validateCreateWithExternalKey(uri).size()>0)
				{
					log.log(Level.SEVERE, String.format("uri validator failed for record ext key: '%s' and path: '%s'  ", uri.getExternalKey(), uri.getUri()));
					throw new WPBIOException("Uri validator failed");
				}
				dataStorage.add(uri);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importWebPage(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBPage webPage = importer.buildWebPage(props);
			if (webPage != null)
			{
				dataStorage.add(webPage);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importWebPageSource(ZipInputStream zis, String path) throws WPBIOException
	{
		try 
		{
			String[] parts = path.split("/");
			String externalKey = parts.length == 3 ? parts[1] : "";
			
			List<WPBPage> pages = dataStorage.query(WPBPage.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (pages.size() == 1)
			{
				WPBPage page = pages.get(0);
				
				byte[] content = getBytesFromInputStream(zis);
				String pageContent = new String(content, "utf-8");
				page.setHtmlSource(pageContent);
				page.setHash(WPBPage.crc32(pageContent));
				dataStorage.update(page);			
			} else
			{
				log.log(Level.SEVERE, "Cannot find web page for " + path);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importWebPageModuleSource(ZipInputStream zis, String path) throws WPBIOException
	{
		try 
		{
			String[] parts = path.split("/");
			String externalKey = parts.length == 3 ? parts[1] : "";
			
			List<WPBPageModule> pageModules = dataStorage.query(WPBPageModule.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (pageModules.size() == 1)
			{
				WPBPageModule pageModule = pageModules.get(0);
				
				byte[] content = getBytesFromInputStream(zis);
				String pageContent = new String(content, "utf-8");
				pageModule.setHtmlSource(pageContent);
				dataStorage.update(pageModule);			
			} else
			{
				log.log(Level.SEVERE, "Cannot find web page module for " + path);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importFileContent(ZipInputStream zis, String path) throws WPBIOException
	{
		try
		{
			String[] parts = path.split("/");
			String externalKey = parts.length > 3 ? parts[1] : "";
			List<WPBFile> files = dataStorage.query(WPBFile.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (files.size() >= 1)
			{
				// just take the first file, normally there should be a single file
				WPBFile file = files.get(0);
				String uniqueId = dataStorage.getUniqueId();
				String cloudPath = uniqueId + "/" + file.getFileName();
				WPBFilePath cloudFile = new WPBFilePath(PUBLIC_BUCKET, cloudPath);
				cloudFileStorage.storeFile(zis, cloudFile);
				cloudFileStorage.updateContentType(cloudFile, file.getAdjustedContentType());
			    WPBFileInfo fileInfo = cloudFileStorage.getFileInfo(cloudFile);
		        file.setBlobKey(cloudFile.getPath());
		        file.setHash(fileInfo.getCrc32());
		        file.setSize(fileInfo.getSize());     
		        if (file.getShortType().compareToIgnoreCase("image") == 0)
		        {
		        	// build the thumbnail for this image
		        	try
		        	{
		        		String thumbnailPath = uniqueId + "/thumnail/" + uniqueId + ".jpg";
		        		WPBFilePath cloudThumbnailFile = new WPBFilePath(PUBLIC_BUCKET, thumbnailPath);
		        		
		        		ByteArrayOutputStream bos = new ByteArrayOutputStream();
				        imageProcessor.resizeImage(cloudFileStorage, cloudFile, 60, "jpg", bos);
				        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());   
						cloudFileStorage.storeFile(bis, cloudThumbnailFile);
						file.setThumbnailBlobKey(cloudThumbnailFile.getPath());
						cloudFileStorage.updateContentType(cloudThumbnailFile, "image/jpg");
				          
						bos.close();
						bis.close();
					} catch (WPBException e)
		        	{
		        		// do nothing as thumbnail fail might because by an unsupported image type
		        		
		        	}
		        }
				dataStorage.update(file);
			} else
			{
				log.log(Level.SEVERE, "Cannot import image for " + path);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);						
		}
	}	
	
	public void importArticleSource(ZipInputStream zis, String path) throws WPBIOException
	{
		try 
		{
			String[] parts = path.split("/");
			String externalKey = parts.length == 3 ? parts[1] : "";
			
			List<WPBArticle> articles = dataStorage.query(WPBArticle.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (articles.size() == 1)
			{
				WPBArticle article = articles.get(0);
				
				byte[] contentBinary = getBytesFromInputStream(zis);
				String stringContent = new String(contentBinary, "utf-8");
				article.setHtmlSource(stringContent);
				dataStorage.update(article);			
			} else
			{
				log.log(Level.SEVERE, "Cannot find article for " + path);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importProject(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBProject project = importer.buildProject(props);
			
			WPBProject tempProject = dataStorage.get(WPBProject.PROJECT_KEY, WPBProject.class);
			if (tempProject == null)
			{
				dataStorage.add(project);
			} else
			{
				dataStorage.update(project);				
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importPageModule(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBPageModule webPageModule = importer.buildWebPageModule(props);
			if (webPageModule != null)
			{
				dataStorage.add(webPageModule);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importArticle(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBArticle article = importer.buildArticle(props);
			if (article != null)
			{
				dataStorage.add(article);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importFile(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBFile file = importer.buildFile(props);
			if (file != null)
			{
				dataStorage.add(file);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importMessage(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBMessage message = importer.buildMessage(props);
			if (message != null)
			{
				dataStorage.add(message);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importParameter(ZipInputStream zis) throws WPBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WPBParameter parameter = importer.buildParameter(props);
			if (parameter != null)
			{
				dataStorage.add(parameter);
			}
		} catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void exportToZip(OutputStream os) throws WPBIOException
	{
		ZipOutputStream zos = new ZipOutputStream(os);
		exportUris(zos, PATH_URIS);
		exportSitePages(zos, PATH_SITE_PAGES);
		exportPageModules(zos, PATH_SITE_PAGES_MODULES);
		exportMessages(zos, PATH_MESSAGES);
		exportFiles(zos, PATH_FILES);
		exportArticles(zos, PATH_ARTICLES);
		exportGlobals(zos, PATH_GLOBALS);
		exportLocales(zos, PATH_LOCALES);
		try
		{
			zos.flush();
			zos.close();
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export project, error flushing/closing stream", e);
		}
	}

	protected void exportLocales(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			WPBProject project = dataStorage.get(WPBProject.PROJECT_KEY, WPBProject.class);
			Map<String, Object> map = new HashMap<String, Object>();
			exporter.export(project, map);								
			String metadataXml = path + "metadata.xml";
			ZipEntry metadataZe = new ZipEntry(metadataXml);
			zos.putNextEntry(metadataZe);
			exportToXMLFormat(map, zos);
			zos.closeEntry();
			
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export uri's to Zip", e);
		}		
	}

	protected void exportGlobals(ZipOutputStream zos, String path) throws WPBIOException
	{
		exportParameters("", zos, path);
	}

	protected void exportUris(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBUri> uris = dataStorage.getAllRecords(WPBUri.class);
			ZipEntry zeExternalGuid = new ZipEntry(path);
			zos.putNextEntry(zeExternalGuid);
			zos.closeEntry();
			for(WPBUri uri: uris)
			{
				String guidPath =  PATH_URIS + uri.getExternalKey() + "/";
				Map<String, Object> map = new HashMap<String, Object>();
				exporter.export(uri, map);								
				String metadataXml = guidPath + "metadata.xml";
				ZipEntry metadataZe = new ZipEntry(metadataXml);
				zos.putNextEntry(metadataZe);
				exportToXMLFormat(map, zos);
				zos.closeEntry();
				
				String parametersPath = String.format(PATH_URI_PARAMETERS, uri.getExternalKey());
				ZipEntry paramsZe = new ZipEntry(parametersPath);
				zos.putNextEntry(paramsZe);
				zos.closeEntry();

				exportParameters(uri.getExternalKey(), zos, parametersPath);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export uri's to Zip", e);
		}
	}
	
	public void exportParameters(String ownerExternalKey, ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBParameter> params = dataStorage.query(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, ownerExternalKey);
			for (WPBParameter parameter: params)
			{
				String paramXmlPath = path + parameter.getExternalKey() + "/" + "metadata.xml";
				Map<String, Object> map = new HashMap<String, Object>();
				exporter.export(parameter, map);								
				ZipEntry metadataZe = new ZipEntry(paramXmlPath);
				zos.putNextEntry(metadataZe);
				exportToXMLFormat(map, zos);
				zos.closeEntry();
			}
			
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export parameters for uri's to Zip", e);
		}
	}
	
	protected void exportSitePages(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBPage> pages = dataStorage.getAllRecords(WPBPage.class);
			for(WPBPage page: pages)
			{
				String pageXmlPath = path + page.getExternalKey() + "/" + "metadata.xml";
				Map<String, Object> map = new HashMap<String, Object>();
				exporter.export(page, map);								
				ZipEntry metadataZe = new ZipEntry(pageXmlPath);
				zos.putNextEntry(metadataZe);
				exportToXMLFormat(map, zos);
				zos.closeEntry();
				String pageSourcePath = path + page.getExternalKey() + "/" + "pageSource.txt";
				String pageSource = page.getHtmlSource() != null ? page.getHtmlSource() : "";
				ZipEntry pageSourceZe = new ZipEntry(pageSourcePath);
				zos.putNextEntry(pageSourceZe);
				zos.write(pageSource.getBytes("UTF-8"));
				zos.closeEntry();
				
				String parametersPath = String.format(PATH_SITE_PAGES_PARAMETERS, page.getExternalKey());
				ZipEntry paramsZe = new ZipEntry(parametersPath);
				zos.putNextEntry(paramsZe);
				zos.closeEntry();

				exportParameters(page.getExternalKey(), zos, parametersPath);

			}
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export site web pages to Zip", e);
		}
	}

	protected void exportArticles(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBArticle> articles = dataStorage.getAllRecords(WPBArticle.class);
			for(WPBArticle article: articles)
			{
				String articleXmlPath = path + article.getExternalKey() + "/" + "metadata.xml";
				Map<String, Object> map = new HashMap<String, Object>();
				exporter.export(article, map);								
				ZipEntry metadataZe = new ZipEntry(articleXmlPath);
				zos.putNextEntry(metadataZe);
				exportToXMLFormat(map, zos);
				zos.closeEntry();
				String articleSourcePath = path + article.getExternalKey() + "/" + "articleSource.txt";
				String pageSource = article.getHtmlSource() != null ? article.getHtmlSource() : "";
				ZipEntry pageSourceZe = new ZipEntry(articleSourcePath);
				zos.putNextEntry(pageSourceZe);
				zos.write(pageSource.getBytes("UTF-8"));
				zos.closeEntry();				
			}
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export articles to Zip", e);
		}
	}

	protected void exportPageModules(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBPageModule> modules = dataStorage.getAllRecords(WPBPageModule.class);
			for(WPBPageModule module: modules)
			{
				String moduleXmlPath = path + module.getExternalKey() + "/" + "metadata.xml";
				Map<String, Object> map = new HashMap<String, Object>();
				exporter.export(module, map);								
				ZipEntry metadataZe = new ZipEntry(moduleXmlPath);
				zos.putNextEntry(metadataZe);
				exportToXMLFormat(map, zos);
				zos.closeEntry();
				String moduleSourcePath = path + module.getExternalKey() + "/" + "moduleSource.txt";
				String moduleSource = module.getHtmlSource() != null ? module.getHtmlSource() : "";
				ZipEntry pageSourceZe = new ZipEntry(moduleSourcePath);
				zos.putNextEntry(pageSourceZe);
				zos.write(moduleSource.getBytes("UTF-8"));
				zos.closeEntry();			
			}
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export site web page modules to Zip", e);
		}
	}

	protected void exportMessages(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBMessage> messages = dataStorage.getAllRecords(WPBMessage.class);
			Map<String, List<WPBMessage>> languageToMessages = new HashMap<String, List<WPBMessage>>();
			for(WPBMessage message: messages)
			{
				String lcid = message.getLcid();
				if (!languageToMessages.containsKey(lcid))
				{
					languageToMessages.put(lcid, new ArrayList<WPBMessage>());
				}
				languageToMessages.get(lcid).add(message);
			}
			
			Set<String> languages = languageToMessages.keySet();
			for(String language: languages)
			{
				for(WPBMessage message: languageToMessages.get(language))
				{
					String messageXmlPath = path + language + "/" + message.getExternalKey() + "/" + "metadata.xml";
					Map<String, Object> map = new HashMap<String, Object>();
					exporter.export(message, map);				
					map.put("lcid", language);
					ZipEntry metadataZe = new ZipEntry(messageXmlPath);
					zos.putNextEntry(metadataZe);
					exportToXMLFormat(map, zos);
					zos.closeEntry();		
				}
			}
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export messages to Zip", e);
		}
	}

	protected void exportFiles(ZipOutputStream zos, String path) throws WPBIOException
	{
		try
		{
			List<WPBFile> files = dataStorage.getAllRecords(WPBFile.class);
			for(WPBFile file: files)
			{
				String fileXmlPath = path + file.getExternalKey() + "/" + "metadata.xml";
				Map<String, Object> map = new HashMap<String, Object>();
				exporter.export(file, map);								
				ZipEntry metadataZe = new ZipEntry(fileXmlPath);
				zos.putNextEntry(metadataZe);
				exportToXMLFormat(map, zos);
				zos.closeEntry();
				String contentPath = String.format(PATH_FILE_CONTENT, file.getExternalKey());
				ZipEntry contentZe = new ZipEntry(contentPath);
				zos.putNextEntry(contentZe);
				zos.closeEntry();
				
				try
				{
					String filePath = contentPath + file.getFileName();
					WPBFilePath cloudFile = new WPBFilePath(PUBLIC_BUCKET, file.getBlobKey());
					InputStream is = cloudFileStorage.getFileContent(cloudFile);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					IOUtils.copy(is, bos);
					bos.flush();
					byte[] content = bos.toByteArray();
					ZipEntry fileZe = new ZipEntry(filePath);
					zos.putNextEntry(fileZe);
					zos.write(content);
					zos.closeEntry();
				} catch (Exception e)
				{
					log.log(Level.SEVERE, " Exporting file :" + file.getExternalKey(), e);
					// do nothing, we do not abort the export because of a failure, but we need to log this as warning
				}
			}
		}
		catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			throw new WPBIOException("Cannot export files to Zip", e);
		}
	}

}