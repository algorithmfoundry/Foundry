/*
 * File:                VectorWriterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class VectorWriterTest extends TestCase
{
    
    public String FILENAME = "DenseVectorWriterTest.txt";
    
    protected Random random = new Random(1);
    
    public VectorWriterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of write method, of class gov.sandia.isrc.math.matrix.mtj.VectorWriter.
     */
    public void testWrite()
    {
        System.out.println("write");
        
        try
        {
            File tempFile = File.createTempFile( FILENAME, null );            
            Writer file = new FileWriter( tempFile );
            VectorWriter instance = new VectorWriter( file );
            Vector vector = VectorFactory.getDefault().copyValues(
                random.nextGaussian(), random.nextGaussian() );
            instance.write( vector );
            instance = null;
            
            Reader fin = new FileReader( tempFile );
            VectorReader reader = new VectorReader( fin );
            Vector result = reader.read();
            assertEquals( vector, result );
        }
        catch (Exception e)
        {
            fail( "Threw exception: " + e );
        }
          

    }

    public void testWriteCollection()
    {
        System.out.println( "writeCollection" );

        try
        {
            File tempFile = File.createTempFile( FILENAME, null );
            Writer file = new FileWriter( tempFile );
            VectorWriter instance = new VectorWriter( file );
            List<Vector> list = Arrays.asList(
                VectorFactory.getDefault().copyValues( random.nextGaussian(), random.nextGaussian() ),
                VectorFactory.getDefault().copyValues( random.nextGaussian(), random.nextGaussian(), random.nextGaussian() ) );
            instance.writeCollection( list );
            instance = null;

            Reader fin = new FileReader( tempFile );
            VectorReader reader = new VectorReader( fin );
            List<Vector> vectors = reader.readCollection(false);
            assertEquals( list.size(), vectors.size() );
            for( int i = 0; i < list.size(); i++ )
            {
                assertEquals( list.get(i), vectors.get(i) );
            }

            reader = new VectorReader(new FileReader( tempFile ));
            try
            {
                reader.readCollection(true);
                fail( "Vectors are different dimensions" );
            }
            catch (Exception e)
            {
                System.out.println( "Good: " + e );
            }

        }
        catch (Exception e)
        {
            fail( "Threw exception: " + e );
        }

    }

    /**
     * Test of getWriter method, of class gov.sandia.isrc.math.matrix.mtj.VectorWriter.
     */
    public void testGetWriter()
    {
        System.out.println("getWriter");
                
        try
        {
            File tempFile = File.createTempFile( FILENAME, null );            
            Writer file = new FileWriter( tempFile );
            VectorWriter instance = new VectorWriter( file );
            assertEquals( file, instance.getWriter() );
        }
        catch (Exception e)
        {
            fail( "Threw exception: " + e );
        }
        
    }

    /**
     * Test of setWriter method, of class gov.sandia.isrc.math.matrix.mtj.VectorWriter.
     */
    public void testSetWriter()
    {
        System.out.println("setWriter");
        
        try
        {
            File tempFile = File.createTempFile( FILENAME, null );            
            Writer f1 = new FileWriter( tempFile );
            VectorWriter instance = new VectorWriter( f1 );
            assertEquals( f1, instance.getWriter() );
            
            File tempFile2 = File.createTempFile( FILENAME + "2", null );            
            Writer f2 = new FileWriter( tempFile2 );
            instance.setWriter( f2 );
            assertTrue( f1 != instance.getWriter() );
            assertEquals( f2, instance.getWriter() );
        }
        catch (Exception e)
        {
            fail( "Threw exception: " + e );
        }
        
    }
    
}
