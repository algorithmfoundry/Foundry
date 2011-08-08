/*
 * File:                FileUtil.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import gov.sandia.cognition.annotation.CodeReview;
import java.io.File;

/**
 * The <code>FileUtil</code> class defines some useful utilities for dealing
 * with files.
 * 
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-11",
    changesNeeded=false,
    comments="No changes necessary."
)
public final class FileUtil 
    extends Object
{

    /**
     * Returns the extension of the given file, which is the part of the name
     * after the last '.'.
     * 
     * @param file
     *            The File to get the extension from.
     * @return The extension part of the given File.
     * @since 1.0
     */
    public static String getExtension(
        final File file)
    {
        return getExtension(file.getName());
    }

    /**
     * Returns the extension of the given filename , which is the part of the
     * name after the last '.'.
     * 
     * @param fileName
     *            The file name to get the extension from.
     * @return The extension part of the given file name.
     * @since 1.0
     */
    public static String getExtension(
        final String fileName)
    {
        String extension = null;
        final int index = fileName.lastIndexOf('.');

        if (index > 0 && index < fileName.length() - 1)
        {
            extension = fileName.substring(index + 1);
        }

        return extension;
    }

    /**
     * Takes a file name and returns the name of the file without the extension
     * on it. The extension is determined by finding the last '.' that appears
     * in the name. If the last '.' is the first character or there is no '.'
     * then the full file name is returned.
     * 
     * @param fileName
     *            A file name.
     * @return The portion of the name before the extension
     * @since 2.0
     */
    public static String removeExtension(
        final String fileName)
    {
        // Find the last index of the extension.
        final int index = fileName.lastIndexOf('.');

        if (index <= 0)
        {
            // No extension so return the whole name.
            return fileName;
        } else
        {
            // Return the part before the extension.
            return fileName.substring(0, index);
        }
    }

    /**
     * Attempts to determine if the application might be able to write to the
     * given file, which may or may not already exists. If the file exists,
     * it just calls the canWrite() method on it. Otherwise, it looks at its
     * parent directory to see if that exists and checks to see if that
     * directory can be written to.
     *
     * @param   file
     *      The file (which may or may not already exist) to see if it can be
     *      written to.
     * @return
     *      True if it should be possible to write to the given file.
     */
    public static boolean couldWrite(
        final File file)
    {
        if (file == null)
        {
            // Bad file.
            return false;
        }
        else if (file.exists())
        {
            // Use the existing can-write check.
            return file.canWrite();
        }
        else
        {
            // Look at the parent directory.
            final File parent = file.getParentFile();
            if (parent != null)
            {
                // See if we can write to the parent directory.
                return parent.canWrite();
            }
            return false;
        }
    }

}
