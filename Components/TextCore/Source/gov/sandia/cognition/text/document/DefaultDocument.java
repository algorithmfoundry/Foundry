/*
 * File:                DefaultDocument.java
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

package gov.sandia.cognition.text.document;

import gov.sandia.cognition.io.FileUtil;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * A default implementation of the {@code Document} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultDocument
    extends AbstractDocument
{

    /**
     * Creates a new {@code DefaultDocument}.
     */
    public DefaultDocument()
    {
        super();

        this.setAccessedDate(System.currentTimeMillis());
    }

    @Override
    public void addField(
        final Field field)
    {
        // Just making this method public.
        super.addField(field);
    }

    @Override
    public void removeField(
        final String fieldName)
    {
        // Just making this field public.
        super.removeField(fieldName);
    }

    /**
     * Sets the title field of the document to the given string.
     *
     * @param   title
     *      The title of the document.
     */
    public void setTitle(
        final String title)
    {
        this.addField(new DefaultTextField(TITLE_FIELD_NAME, title));
    }

    /**
     * Sets the last modified date of the document.
     *
     * @param   time
     *      The last modified date in milliseconds.
     */
    public void setLastModifiedDate(
        final long time)
    {
        this.setLastModifiedDate(new Date(time));
    }

    /**
     * Sets the last modified date of the document.
     *
     * @param   date
     *      The last modified date.
     */
    public void setLastModifiedDate(
        final Date date)
    {
        this.addField(new DefaultDateField(LAST_MODIFIED_DATE_FIELD_NAME, date));
    }

    /**
     * Sets the last accessed date of the document.
     *
     * @param   time
     *      The last accessed date in milliseconds.
     */
    public void setAccessedDate(
        final long time)
    {
        this.setAccessedDate(new Date(time));
    }

    /**
     * Sets the last accessed date of the document.
     *
     * @param   date
     *      The last accessed date.
     */
    public void setAccessedDate(
        final Date date)
    {
        this.addField(new DefaultDateField(ACCESSED_DATE_FIELD_NAME, date));
    }

    /**
     * Sets the author field of the document to the given string.
     *
     * @param   author
     *      The author of the document.
     */
    public void setAuthor(
        final String author)
    {
        this.addField(new DefaultTextField(AUTHOR_FIELD_NAME, author));
    }

    /**
     * Sets the body field of the document to the given string.
     *
     * @param   body
     *      The body text of the document.
     */
    public void setBody(
        final String body)
    {
        this.addField(new DefaultTextField(BODY_FIELD_NAME, body));
    }

    /**
     * Reads the file name and title from the given URL. It uses the full
     * file name and then the local file name without the file extension is
     * used as the title.
     *
     * @param   connection
     *      The connection to read the metadata from.
     */
    public void readMetaData(
        final URLConnection connection)
    {
        final URL url = connection.getURL();
        final String fileFullName = url.getFile();
        final String fileName = new File(fileFullName).getName();

        this.setName(fileName);
        this.setTitle(FileUtil.removeExtension(fileName));

        this.setLastModifiedDate(connection.getLastModified());
    }
}
