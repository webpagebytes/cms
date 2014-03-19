package com.webbricks.datautility;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.webbricks.exception.WBException;

public class WBImageProcessor {

	public WBImageProcessor() {
	}
	public boolean resizeImage(WBCloudFileStorage cloudStorage, WBCloudFile cloudFile, int desiredSize, String outputFormat, OutputStream os) throws WBException
	{
		InputStream is = null;
		try
		{
			//get the file content
			WBCloudFileInfo fileInfo = cloudStorage.getFileInfo(cloudFile);
			String type = fileInfo.getContentType().toLowerCase();
			if (!type.startsWith("image"))
			{
				return false;
			}
			is = cloudStorage.getFileContent(cloudFile);
	  		BufferedImage bufImg = ImageIO.read(is);
    		Dimension<Integer> newSize = getResizeSize(bufImg.getWidth(), bufImg.getHeight(), desiredSize);
    		BufferedImage bdest = new BufferedImage(newSize.getX(), newSize.getY(), BufferedImage.TYPE_INT_RGB);
    		Graphics2D g = bdest.createGraphics();
    		Dimension<Double> scale = getResizeScale(bufImg.getHeight(), bufImg.getWidth(), desiredSize);
    		AffineTransform at = AffineTransform.getScaleInstance(scale.getX(), scale.getY());
    		g.drawRenderedImage(bufImg, at);
    		ImageIO.write(bdest, outputFormat, os);		
    		return true;
		} catch (Exception e)
		{
			throw new WBException("Cannot resize image ", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}

	}
	
	
	private static Dimension<Integer> getResizeSize(int X, int Y, int desiredSize)
	{
		if (desiredSize <= 0 || X <= 0 || Y <= 0) 
		{
			return new Dimension<Integer>(X, Y);
		}
		double ratio = X/(double) Y;
		if (X > Y)
		{
			return new Dimension<Integer>(desiredSize, new Double(desiredSize/ratio).intValue());
		} else
		{
			return new Dimension<Integer>(new Double(desiredSize*ratio).intValue(), desiredSize);
		}
	}
	
	private static Dimension<Double> getResizeScale(int X, int Y, int desiredSize)
	{
		if (desiredSize <= 0 || X <= 0 || Y <= 0) 
		{
			return new Dimension<Double>(1.0, 1.0);
		}
		double ratio = X/ (double) Y;
		if (X > Y)
		{
			return new Dimension<Double>((double)desiredSize/X, (double)(ratio*desiredSize)/Y);
		} else
		{
			return new Dimension<Double>((double)(ratio*desiredSize)/X, (double)desiredSize/Y);
		}	
	}
  

}
