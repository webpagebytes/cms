package com.webpagebytes.cms.utility;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestContentTypeDetector {

@Test
public void test_contentTypeToShortType()
{
	assertTrue(ContentTypeDetector.contentTypeToShortType("image/png").equals(ContentTypeDetector.IMAGE_CONTENT_NAME));
	
	//upper case content type
	assertTrue(ContentTypeDetector.contentTypeToShortType("Image/Png").equals(ContentTypeDetector.IMAGE_CONTENT_NAME));
	assertTrue(ContentTypeDetector.contentTypeToShortType("video/mp4").equals(ContentTypeDetector.VIDEO_CONTENT_NAME));
	
	assertTrue(ContentTypeDetector.contentTypeToShortType("application/octet-stream").equals(ContentTypeDetector.APP_CONTENT_NAME));
	assertTrue(ContentTypeDetector.contentTypeToShortType("audio/mp3").equals(ContentTypeDetector.AUDIO_CONTENT_NAME));
	
	assertTrue(ContentTypeDetector.contentTypeToShortType("unknown/unknown").equals(ContentTypeDetector.APP_CONTENT_NAME));

}

@Test
public void test_fileNameToContentType()
{
	// known content type file
	assertTrue(ContentTypeDetector.fileNameToContentType("x.png").equals("image/png"));
	
	// no file extension
	assertTrue(ContentTypeDetector.fileNameToContentType("x").equals(ContentTypeDetector.DEFAULT_TYPE));

	// file with not supported content type
	assertTrue(ContentTypeDetector.fileNameToContentType("x.xyz").equals(ContentTypeDetector.DEFAULT_TYPE));

	// content type not handled
	assertTrue(ContentTypeDetector.fileNameToContentType("application/ogg").equals(ContentTypeDetector.DEFAULT_TYPE));
}

@Test
public void test_constructor()
{
	ContentTypeDetector d = new ContentTypeDetector();
	assertTrue( d!=null );
}
}
