/*
 * File:                TextDocumentExtractor.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document.extractor;

import gov.sandia.cognition.io.FileUtil;
import gov.sandia.cognition.text.document.DefaultDocument;
import gov.sandia.cognition.text.document.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Extracts text from plain text documents.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class TextDocumentExtractor
    extends AbstractSingleDocumentExtractor
{
    /** The content type is {@value}. */
    public static final String CONTENT_TYPE = "text/plain";

    /** The default set of file extensions for text files. */
    public static final List<String> DEFAULT_TEXT_FILE_EXTENSIONS =
        Collections.unmodifiableList(Arrays.asList(new String[] { "txt", "text" }));

    /**
     * Creates a new {@code TextDocumentExtractor}.
     */
    public TextDocumentExtractor()
    {
        super();
    }

    public boolean canExtract(
        final URI uri)
        throws IOException
    {
        // Get the extension of the file.
        final String fileName = uri.getPath();
        final String fileExtension = FileUtil.getExtension(fileName);

        // Match the extension to our list of known extensions.
        if (fileExtension != null)
        {
            final String fileExtensionLowerCase = fileExtension.toLowerCase();

            return DEFAULT_TEXT_FILE_EXTENSIONS.contains(fileExtensionLowerCase);
        }

        return false;
    }
    
    public boolean canExtract(
        final URLConnection connection)
        throws IOException
    {
        // Make sure the content types match.
        final String contentType = connection.getContentType();
        return CONTENT_TYPE.equals(contentType);
    }

    public Document extractDocument(
        final URLConnection connection)
        throws IOException
    {
        // We are going to read the body from the connection.
        final StringBuffer body = new StringBuffer();
        final BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        try
        {
            String s = null;
            while ((s = reader.readLine()) != null)
            {
                body.append(s);
                body.append("\n");
            }
        }
        finally
        {
            reader.close();
        }

        // Create the resulting document.
        final DefaultDocument result = new DefaultDocument();

        // Set the meta-data based on the connection.
        result.readMetaData(connection);

        // Set the body that we read in.
        result.setBody(body.toString());

        return result;
    }
}
