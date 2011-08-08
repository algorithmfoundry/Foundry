/*
 * MatrixWriterTest.java
 * JUnit based test
 *
 * Created on May 16, 2006, 7:18 AM
 */

package gov.sandia.cognition.math.matrix;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import junit.framework.*;
import java.io.Writer;
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 */
public class MatrixWriterTest extends TestCase
{
    
    static String FILENAME = "DenseMatrixWriterTest.txt";
    protected Random random = new Random();
    
    public MatrixWriterTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MatrixWriterTest.class);
        
        return suite;
    }

    /**
     * Test of write method, of class gov.sandia.isrc.math.matrix.mtj.MatrixWriter.
     */
    public void testWrite() throws Exception
    {
        System.out.println("write");
        
        try
        {
            File tempFile = File.createTempFile( FILENAME, null );
            Writer file = new FileWriter( tempFile );
            MatrixWriter instance = new MatrixWriter( file );
            int M = 5;
            int N = 3;
            double range = 10;
            Matrix vector = MatrixFactory.getDefault().createUniformRandom( M, N, -range, range, random );
            instance.write( vector );
            instance = null;
            
            FileReader fin = new FileReader( tempFile );
            MatrixReader reader = new MatrixReader( fin );
            Matrix result = reader.read();
            assertEquals( vector, result );
            (new File( FILENAME )).delete();
            
        }
        catch (Exception e)
        {
            fail( "Threw exception: " + e );
        }
    }

    /**
     * Test of getWriter method, of class gov.sandia.isrc.math.matrix.mtj.MatrixWriter.
     */
    public void testGetWriter()
    {
        System.out.println("getWriter");
        
        try
        {
            File tempFile = File.createTempFile( FILENAME, null );
            Writer f1 = new FileWriter( tempFile );
            MatrixWriter instance = new MatrixWriter( f1 );
            assertEquals( f1, instance.getWriter() );
        }
        catch (Exception e)
        {
            fail( "Threw exception: " + e );
        }
    }

    /**
     * Test of setWriter method, of class gov.sandia.isrc.math.matrix.mtj.MatrixWriter.
     */
    public void testSetWriter()
    {
        System.out.println("setWriter");
        
        try
        {
            File tempFile = File.createTempFile( FILENAME, null );
            Writer f1 = new FileWriter( tempFile );
            MatrixWriter instance = new MatrixWriter( f1 );
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
