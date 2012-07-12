/*
 * File:                XMLSerializationHandlerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.io;

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for XMLSerializationHandlerTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class XStreamSerializationHandlerTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    public XStreamSerializationHandlerTest(String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        XStreamSerializationHandler x = new XStreamSerializationHandler();
        assertNotNull( x );
    }

    /**
     * Test of write method, of class gov.sandia.isrc.io.XMLSerializationHandler.
     */
    public void testWriteMatrix() throws Exception
    {
        System.out.println("writeReadMatrix");
        
        Matrix m1 = MatrixFactory.getDefault().createUniformRandom(
            3, 4, -3.0, 3.0, RANDOM);
        StringWriter writer = new StringWriter();
        
        XStreamSerializationHandler.write( writer, m1 );
        
        StringReader reader = new StringReader( writer.toString() );
        DenseMatrix m2 = null;
        try
        {
            m2 = (DenseMatrix) XStreamSerializationHandler.read( reader );        
        }
        catch (Exception e)
        {
            fail( "Couldn't read DenseMatrix from String: " + writer.toString() );
        }
        
        assertEquals( m1, m2 );
        
    }
    
    /**
     * Test of write method, of class gov.sandia.isrc.io.XMLSerializationHandler.
     */
    public void testWriteList() throws Exception
    {
        System.out.println("writeReadList");
        
        int N = RANDOM.nextInt(100) + 3;
        LinkedList<Ring<?>> list = new LinkedList<Ring<?>>();
        for( int i = 0; i < N; i++ )
        {
            if( RANDOM.nextBoolean() )
            {
                list.add( MatrixFactory.getDefault().createUniformRandom(
                    2, 3, -3, 3, RANDOM ) );
            }
            else
            {
                list.add( new ComplexNumber(
                    RANDOM.nextGaussian(), RANDOM.nextGaussian() ) );
            }
        }
        
        StringWriter writer = new StringWriter();
        XStreamSerializationHandler.write( writer, list );
        
        StringReader reader = new StringReader( writer.toString() );
        LinkedList<?> list2 = null;
        try
        {
            list2 = (LinkedList<?>) XStreamSerializationHandler.read( reader );
        }
        catch (Exception e)
        {
            fail( "Couldn't read LinkedList from String: " + writer.toString() );
        }
        
        assertEquals( list.size(), list2.size() );
        for( int i = 0; i < list2.size(); i++ )
        {
            Ring<?> r1 = list.get(i);
            Ring<?> r2 = (Ring<?>) list2.get(i);
            assertEquals( r1, r2 );
        }
        
    }    
    
    /**
     * Test of write method, of class gov.sandia.isrc.io.XMLSerializationHandler.
     */
    public void testWriteMultiple()
        throws Exception
    {
        System.out.println("writeReadMultiple");

        int N = 2;
        LinkedList<Ring<?>> list = new LinkedList<Ring<?>>();
        for( int i = 0; i < N; i++ )
        {
            if( RANDOM.nextBoolean() )
            {
                list.add( MatrixFactory.getDefault().createUniformRandom(
                    2, 3, -3, 3, RANDOM ) );
            }
            else
            {
                list.add( new ComplexNumber(
                    RANDOM.nextGaussian(), RANDOM.nextGaussian() ) );
            }
        }

        StringWriter writer = new StringWriter();
        for( int i = 0; i < N; i++ )
        {
            XStreamSerializationHandler.write( writer, list.get(i) );
        }
        
        StringReader reader = new StringReader( writer.toString() );
        int numRead = 0;
            
        // We should be able to read the first object fine
        Ring<?> o = null;
        try
        {
            o = (Ring<?>) XStreamSerializationHandler.read( reader );
        }
        catch (Exception e)
        {
            System.out.println( "Exception: " + e );
            fail( numRead + ": Couldn't read " + list.get(numRead).getClass() + " from String: " + writer.toString() );
        }
        
        // But we should barf on the second object in the file
        // For details about why, see http://xstream.codehaus.org/objectstream.html
        numRead++;
        try
        {
            o = (Ring) XStreamSerializationHandler.read( reader );
            fail( "XStream XML files should only have ONE object written to them." );
        }
        catch (Exception e)
        {
            System.out.println( "Proper Exception: " + e );
        }
        
    }


    /**
     * String conversion
     */
    public void testStringConversion()
        throws IOException
    {
        System.out.println( "String conversion" );
        Vector v = VectorFactory.getDefault().createUniformRandom(
            4, -1.0, 1.0, RANDOM);

        String sv = XStreamSerializationHandler.convertToString(v);
        System.out.println( "Vector XStream:\n" + sv );

        Vector vhat = (Vector) XStreamSerializationHandler.convertFromString( sv );
        System.out.println( "vhat:\n" + vhat );
        assertEquals( v, vhat );
    }

    public void testFileConversion()
        throws IOException
    {
        System.out.println( "File conversion" );

        File file = File.createTempFile("REMOVE.XStreamSerializationHandlerTest", "txt" );
        Vector v = VectorFactory.getDefault().createUniformRandom(
            4, -1.0, 1.0, RANDOM);

        System.out.println( "File: " + file.getAbsolutePath() );
        XStreamSerializationHandler.writeToFile(file.getAbsoluteFile(), v);

        Vector vhat = (Vector) XStreamSerializationHandler.readFromFile( file.getAbsoluteFile() );
        assertEquals( v, vhat );

        try
        {
            XStreamSerializationHandler.writeToFile( (File) null, v);
            fail( "File cannot be null" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            XStreamSerializationHandler.writeToFile(file, null);
            fail( "Cannot write null object" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
