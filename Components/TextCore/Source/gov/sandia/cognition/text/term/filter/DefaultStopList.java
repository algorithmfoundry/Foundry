/*
 * File:                DefaultStopList.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 09, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.Termable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A default, case-insensitive stop-list.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultStopList
    extends AbstractCloneableSerializable
    implements StopList
{

    /** The set of words in the stop list, all in lower-case. */
    protected Set<String> words;

    /**
     * Creates a new, empty {@code DefaultStopList}.
     */
    public DefaultStopList()
    {
        super();

        this.setWords(new LinkedHashSet<String>());
    }

    /**
     * Creates a new {@code DefaultStopList} with the given set of words.
     *
     * @param   words
     *      The words to add to the stop list.
     */
    public DefaultStopList(
        final Iterable<String> words)
    {
        this();

        this.addAll(words);
    }

    @Override
    public DefaultStopList clone()
    {
        final DefaultStopList clone = (DefaultStopList) super.clone();
        clone.words = new LinkedHashSet<String>(this.words);

        return clone;
    }

    /**
     * Adds a word to the stop list.
     *
     * @param   word
     *      The word to add to the stop list.
     */
    public void add(
        final String word)
    {
        this.words.add(word.toLowerCase());
    }

    /**
     * Adds all of the given words to the stop list.
     *
     * @param   words
     *      The words to add.
     */
    public void addAll(
        final Iterable<String> words)
    {
        for (String word : words)
        {
            this.add(word);
        }
    }

    public boolean contains(
        final Termable term)
    {
        return this.contains(term.asTerm());
    }

    /**
     * Returns true if the given term is in the stop list.
     *
     * @param   term
     *      A term.
     * @return
     *      True if the term is contained in the stop list. Otherwise, false.
     */
    public boolean contains(
        final Term term)
    {
        final String text = term.getName();
        return this.contains(text);
    }

    /**
     * Returns true if the given word is in the stop list.
     *
     * @param   word
     *      A word.
     * @return
     *      True if the word is contained in the stop list. Otherwise, false.
     */
    public boolean contains(
        final String word)
    {
        return this.words.contains(word.toLowerCase());
    }

    /**
     * Gets the set of words in the stop list.
     *
     * @return
     *      The set of words in the stop list.
     */
    public Set<String> getWords()
    {
        return Collections.unmodifiableSet(this.words);
    }

    /**
     * Sets the set of words in the stop list.
     *
     * @param   words
     *      The set of words in the stop list.
     */
    protected void setWords(
        final Set<String> words)
    {
        this.words = words;
    }

    /**
     * Saves the stop list to the given file. Each word is written on a
     * separate line.
     *
     * @param   file
     *      The file to save the stop list to.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public void saveAsText(
        final File file)
        throws IOException
    {
        final PrintStream out = new PrintStream(file);
        try
        {
            this.saveAsText(out);
        }
        finally
        {
            out.close();
        }
    }

    /**
     * Saves the stop list to the given stream. Each word is written on a
     * separate line. The stream is not closed at the end.
     *
     * @param   out
     *      The stream to write the stop words to.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public void saveAsText(
        final PrintStream out)
        throws IOException
    {
        for (String word : this.getWords())
        {
            out.println(word);
        }
    }

    /**
     * Loads a stop list by reading in a given file and treating each line as
     * a word.
     *
     * @param file
     *      The file to read in.
     * @return
     *      A new stop list containing a stop word for each line in the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public static DefaultStopList loadFromText(
        final File file)
        throws IOException
    {
        return loadFromText(file.toURI());
    }

    /**
     * Loads a stop list by reading in a given file and treating each line as
     * a word.
     *
     * @param uri
     *      The file to read in.
     * @return
     *      A new stop list containing a stop word for each line in the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public static DefaultStopList loadFromText(
        final URI uri)
        throws IOException
    {
        return loadFromText(uri.toURL());
    }

    /**
     * Loads a stop list by reading in a given file and treating each line as
     * a word.
     *
     * @param url
     *      The file to read in.
     * @return
     *      A new stop list containing a stop word for each line in the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public static DefaultStopList loadFromText(
        final URL url)
        throws IOException
    {
        return loadFromText(url.openConnection());
    }

    /**
     * Loads a stop list by reading in a given file and treating each line as
     * a word.
     *
     * @param connection
     *      The connection to the file to read in.
     * @return
     *      A new stop list containing a stop word for each line in the file.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public static DefaultStopList loadFromText(
        final URLConnection connection)
        throws IOException
    {
        // Open a reader on the input stream.
        final BufferedReader reader =
            new BufferedReader(new InputStreamReader(
            connection.getInputStream()));

        try
        {
            // Attempt to load the stop list from the reader.
            return loadFromText(reader);
        }
        finally
        {
            reader.close();
        }
    }

    /**
     * Loads a stop list by reading in from the given reader and treating each
     * line as a word.
     *
     * @param   reader
     *      The reader to read the stop words from. Does not close the reader.
     * @return
     *      A new stop list containing a stop word for each line in the reader.
     * @throws java.io.IOException
     *      If there is an IO error.
     */
    public static DefaultStopList loadFromText(
        final BufferedReader reader)
        throws IOException
    {
        // Create the stop list to hold the result.
        final DefaultStopList result = new DefaultStopList();

        // Read through each line.
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            line = line.trim();

            if (line.length() > 0)
            {
                result.add(line);
            }
        }

        // We've read through the whole reader so return the result.
        return result;
    }

}
