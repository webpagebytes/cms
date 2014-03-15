package com.webbricks.datautility;

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

import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBExporter;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBImporter;
import com.webbricks.cmsdata.WBMessage;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.exception.WBIOException;

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
	
	public static final String PATH_ARTICLES = "aticles/";
	public static final String PATH_GLOBALS = "settings/globals/";
	public static final String PATH_LOCALES = "settings/locales/";
	
	
	private WBExporter exporter = new WBExporter();
	private WBImporter importer = new WBImporter();
	
	private AdminDataStorage dataStorage = AdminDataStorageFactory.getInstance();
	private WBCloudFileStorage cloudFileStorage = WBCloudFileStorageFactory.getInstance();
	
	
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
	

	public void importFromZip(InputStream is) throws WBIOException
	{
		ZipInputStream zis = new ZipInputStream(is);
	
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
			throw new WBIOException("cannot import from  zip", e);
		}
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
	
	public void importUri(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBUri uri = importer.buildUri(props);
			if (uri != null)
			{
				dataStorage.add(uri);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importWebPage(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBWebPage webPage = importer.buildWebPage(props);
			if (webPage != null)
			{
				dataStorage.add(webPage);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importWebPageSource(ZipInputStream zis, String path) throws WBIOException
	{
		try 
		{
			String[] parts = path.split("/");
			String externalKey = parts.length == 3 ? parts[1] : "";
			
			List<WBWebPage> pages = dataStorage.query(WBWebPage.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (pages.size() == 1)
			{
				WBWebPage page = pages.get(0);
				
				byte[] content = getBytesFromInputStream(zis);
				String pageContent = new String(content, "utf-8");
				page.setHtmlSource(pageContent);
				page.setHash(WBWebPage.crc32(pageContent));
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

	public void importWebPageModuleSource(ZipInputStream zis, String path) throws WBIOException
	{
		try 
		{
			String[] parts = path.split("/");
			String externalKey = parts.length == 3 ? parts[1] : "";
			
			List<WBWebPageModule> pageModules = dataStorage.query(WBWebPageModule.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (pageModules.size() == 1)
			{
				WBWebPageModule pageModule = pageModules.get(0);
				
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

	public void importFileContent(ZipInputStream zis, String path) throws WBIOException
	{
		try
		{
			String[] parts = path.split("/");
			String externalKey = parts.length > 3 ? parts[1] : "";
			List<WBFile> files = dataStorage.query(WBFile.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (files.size() >= 1)
			{
				// just take the first file, normally there should be a single file
				WBFile file = files.get(0);
				String cloudPath = dataStorage.getUniqueId() + "/" + file.getFileName();
				WBCloudFile cloudFile = new WBCloudFile("public", cloudPath);
				cloudFileStorage.storeFile(zis, cloudFile);
			    WBCloudFileInfo fileInfo = cloudFileStorage.getFileInfo(cloudFile);
		        file.setBlobKey(cloudFile.getPath());
		        file.setHash(fileInfo.getCrc32());
		        file.setSize(fileInfo.getSize());     
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
	
	public void importArticleSource(ZipInputStream zis, String path) throws WBIOException
	{
		try 
		{
			String[] parts = path.split("/");
			String externalKey = parts.length == 3 ? parts[1] : "";
			
			List<WBArticle> articles = dataStorage.query(WBArticle.class, "externalKey", AdminQueryOperator.EQUAL, externalKey);
			if (articles.size() == 1)
			{
				WBArticle article = articles.get(0);
				
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

	public void importProject(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBProject project = importer.buildProject(props);
			
			WBProject tempProject = dataStorage.get(WBProject.PROJECT_KEY, WBProject.class);
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

	public void importPageModule(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBWebPageModule webPageModule = importer.buildWebPageModule(props);
			if (webPageModule != null)
			{
				dataStorage.add(webPageModule);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importArticle(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBArticle article = importer.buildArticle(props);
			if (article != null)
			{
				dataStorage.add(article);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importFile(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBFile file = importer.buildFile(props);
			if (file != null)
			{
				dataStorage.add(file);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importMessage(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBMessage message = importer.buildMessage(props);
			if (message != null)
			{
				dataStorage.add(message);
			}
		} catch (IOException e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

	public void importParameter(ZipInputStream zis) throws WBIOException
	{
		try 
		{
			Map<Object, Object> props = importFromXMLFormat(zis);
			WBParameter parameter = importer.buildParameter(props);
			if (parameter != null)
			{
				dataStorage.add(parameter);
			}
		} catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void exportToZip(OutputStream os) throws WBIOException
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
			throw new WBIOException("Cannot export project, error flushing/closing stream", e);
		}
	}

	protected void exportLocales(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			WBProject project = dataStorage.get(WBProject.PROJECT_KEY, WBProject.class);
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
			throw new WBIOException("Cannot export uri's to Zip", e);
		}		
	}

	protected void exportGlobals(ZipOutputStream zos, String path) throws WBIOException
	{
		exportParameters("", zos, path);
	}

	protected void exportUris(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBUri> uris = dataStorage.getAllRecords(WBUri.class);
			ZipEntry zeExternalGuid = new ZipEntry(path);
			zos.putNextEntry(zeExternalGuid);
			zos.closeEntry();
			for(WBUri uri: uris)
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
			throw new WBIOException("Cannot export uri's to Zip", e);
		}
	}
	
	public void exportParameters(String ownerExternalKey, ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBParameter> params = dataStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, ownerExternalKey);
			for (WBParameter parameter: params)
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
			throw new WBIOException("Cannot export parameters for uri's to Zip", e);
		}
	}
	
	protected void exportSitePages(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBWebPage> pages = dataStorage.getAllRecords(WBWebPage.class);
			for(WBWebPage page: pages)
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
			throw new WBIOException("Cannot export site web pages to Zip", e);
		}
	}

	protected void exportArticles(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBArticle> articles = dataStorage.getAllRecords(WBArticle.class);
			for(WBArticle article: articles)
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
			throw new WBIOException("Cannot export articles to Zip", e);
		}
	}

	protected void exportPageModules(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBWebPageModule> modules = dataStorage.getAllRecords(WBWebPageModule.class);
			for(WBWebPageModule module: modules)
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
			throw new WBIOException("Cannot export site web page modules to Zip", e);
		}
	}

	protected void exportMessages(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBMessage> messages = dataStorage.getAllRecords(WBMessage.class);
			Map<String, List<WBMessage>> languageToMessages = new HashMap<String, List<WBMessage>>();
			for(WBMessage message: messages)
			{
				String lcid = message.getLcid();
				if (!languageToMessages.containsKey(lcid))
				{
					languageToMessages.put(lcid, new ArrayList<WBMessage>());
				}
				languageToMessages.get(lcid).add(message);
			}
			
			Set<String> languages = languageToMessages.keySet();
			for(String language: languages)
			{
				for(WBMessage message: languageToMessages.get(language))
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
			throw new WBIOException("Cannot export messages to Zip", e);
		}
	}

	protected void exportFiles(ZipOutputStream zos, String path) throws WBIOException
	{
		try
		{
			List<WBFile> files = dataStorage.getAllRecords(WBFile.class);
			for(WBFile file: files)
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
					WBCloudFile cloudFile = new WBCloudFile("public", file.getBlobKey());
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
			throw new WBIOException("Cannot export files to Zip", e);
		}
	}

}