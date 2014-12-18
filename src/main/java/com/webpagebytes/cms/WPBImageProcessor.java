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

package com.webpagebytes.cms;
import java.io.OutputStream;

import com.webpagebytes.cms.exception.WPBException;

/**
 * <p>
 * Interface to resize images stored in the WPBFileStorage
 * </p>
 * <p>
 * An application that uses Webpagebytes CMS will have to use a concrete implementation of WPBImageProcessor in the application configuration file.
 * </p>
 */
public interface WPBImageProcessor {
    /**
     * Method that returns an image with a specified size as an OutputStream
     * @param fileStorage File storage where the file to be resized is localed
     * @param filepath Path of the file to be resized
     * @param desiredSize Desired size of the new image
     * @param outputFormat Output format of the image
     * @param os OutpuStream that contains the resized image
     * @return true if the image was resized, false otherwise
     * @throws WPBException
     */
    public boolean resizeImage(WPBFileStorage fileStorage, WPBFilePath filepath, int desiredSize, String outputFormat, OutputStream os) throws WPBException;
    
}
