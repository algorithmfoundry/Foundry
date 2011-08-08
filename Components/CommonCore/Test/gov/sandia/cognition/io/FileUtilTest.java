/*
 * File:                FileUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import gov.sandia.cognition.collection.CollectionUtil;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: FileUtil
 * 
 * @author Justin Basilico
 * @since 2.0
 */
public class FileUtilTest
    extends TestCase
{

    /**
     * Constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        FileUtil f = new FileUtil();
        assertNotNull( f );
    }

    /**
     * Test of getExtension method, of class gov.sandia.cognition.io.FileUtil.
     */
    public void testGetExtension()
    {
        assertEquals("txt", FileUtil.getExtension("test.txt"));
        assertEquals("TXT", FileUtil.getExtension("test.TXT"));
        assertNull(FileUtil.getExtension("test"));
        assertEquals("Periods", FileUtil
                .getExtension("this.is.a.lot.of.Periods"));

        assertEquals("txt", FileUtil.getExtension(new File("test.txt")));

        boolean exceptionThrown = false;
        try
        {
            FileUtil.getExtension((String) null);
        } catch (final NullPointerException e)
        {
            exceptionThrown = true;
        } finally
        {
            assertTrue(exceptionThrown);
        }

        assertNull( FileUtil.getExtension("noextension") );


    }

    /**
     * Test of removeExtension method, of class
     * gov.sandia.cognition.io.FileUtil.
     */
    public void testRemoveExtension()
    {
        assertEquals("test", FileUtil.removeExtension("test.txt"));
        assertEquals("test", FileUtil.removeExtension("test.TXT"));
        assertEquals("Test", FileUtil.removeExtension("Test"));
        assertEquals("this.is.a.lot.of", FileUtil
                .removeExtension("this.is.a.lot.of.periods"));

        boolean exceptionThrown = false;
        try
        {
            FileUtil.removeExtension((String) null);
        } catch (final NullPointerException e)
        {
            exceptionThrown = true;
        } finally
        {
            assertTrue(exceptionThrown);
        }

    }

    /**
     * Test of couldWrite
     */
    public void testCouldWrite()
    {
        System.out.println( "couldWrite" );
        assertFalse( FileUtil.couldWrite(null) );

        assertFalse( FileUtil.couldWrite(new File( "testingWrite.txt" )) );
        FileUtil.couldWrite( new File( "/testingWrite.txt" ) );
    }

    /**
     * readFile
     */
    public void testReadFile() throws IOException
    {
        System.out.println( "readFile" );

        File file = File.createTempFile("REMOVE.FileUtilTest", "txt" );
        Writer writer = new FileWriter(file);
        String[] tokens = { "This", "is", "a", "test" };

        for( int i = 0; i < tokens.length; i++ )
        {
            writer.write( tokens[i] );
            if( i < tokens.length-1 )
            {
                writer.write( "," );
            }
        }
        writer.close();

        LinkedList<String[]> result = CSVUtility.readFile(file.getAbsolutePath());
        assertEquals( 1, result.size() );
        String[] resultTokens = result.get(0);
        assertEquals( tokens.length, resultTokens.length );
        for( int i = 0; i < tokens.length; i++ )
        {
            assertEquals( tokens[i], resultTokens[i] );
        }

    }

}
