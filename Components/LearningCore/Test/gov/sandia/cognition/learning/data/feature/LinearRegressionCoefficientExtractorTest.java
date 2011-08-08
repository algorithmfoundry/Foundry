/*
 * File:                LinearRegressionCoefficientExtractorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.data.feature;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class LinearRegressionCoefficientExtractorTest extends TestCase
{

    public LinearRegressionCoefficientExtractorTest( String testName )
    {
        super( testName );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.util.function.LinearRegressionEvaluator.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        LinearRegressionCoefficientExtractor instance = new LinearRegressionCoefficientExtractor( 20 );
        LinearRegressionCoefficientExtractor clone = instance.clone();
        assertNotSame( instance, clone );
        assertEquals( instance.getMaxBufferSize(), clone.getMaxBufferSize() );
        assertNotSame( instance.getState(), clone.getState() );

    }

    /**
     * Test of getMaxBufferSize method, of class gov.sandia.cognition.learning.util.function.LinearRegressionEvaluator.
     */
    public void testGetMaxBufferSize()
    {
        System.out.println( "getMaxBufferSize" );

        int size = (int) (Math.random() * 100) + 2;
        LinearRegressionCoefficientExtractor instance = new LinearRegressionCoefficientExtractor( size );

        assertEquals( size, instance.getMaxBufferSize() );

    }

    /**
     * Test of setMaxBufferSize method, of class gov.sandia.cognition.learning.util.function.LinearRegressionEvaluator.
     */
    public void testSetMaxBufferSize()
    {
        System.out.println( "setMaxBufferSize" );

        int size = (int) (Math.random() * 100) + 2;
        LinearRegressionCoefficientExtractor instance = new LinearRegressionCoefficientExtractor( size );
        assertEquals( size, instance.getMaxBufferSize() );

        int s2 = size + 1;
        instance.setMaxBufferSize( s2 );
        assertEquals( s2, instance.getMaxBufferSize() );

        try
        {
            instance.setMaxBufferSize( 1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        instance.setMaxBufferSize( 2 );
    }

    /**
     * Test of createDefaultState method, of class gov.sandia.cognition.learning.util.function.LinearRegressionEvaluator.
     */
    public void testCreateDefaultState()
    {
        System.out.println( "createDefaultState" );

        LinearRegressionCoefficientExtractor instance = new LinearRegressionCoefficientExtractor( 20 );
        assertNotNull( instance.getState() );
        assertNotNull( instance.createDefaultState() );
        assertEquals( 0, instance.getState().size() );
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.LinearRegressionEvaluator.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );

        int maxBufferSize = 5;
        LinearRegressionCoefficientExtractor instance =
            new LinearRegressionCoefficientExtractor( maxBufferSize );
        instance.evaluate( new Vector3( 0.0, 0.0, 0.0 ) );
        instance.evaluate( new Vector3( 0.0, 0.0, 0.0 ) );
        instance.evaluate( new Vector3( 0.0, 0.0, 0.0 ) );
        instance.evaluate( new Vector3( 0.0, 0.0, 0.0 ) );
        instance.evaluate( new Vector3( 0.0, 0.0, 0.0 ) );
        instance.evaluate( new Vector3( 0.0, 10.0, 0.0 ) );
        instance.evaluate( new Vector3( 0.0, 12.0, 2.0 ) );
        instance.evaluate( new Vector3( 0.0, 14.0, 1.0 ) );
        instance.evaluate( new Vector3( 0.0, 16.0, 2.0 ) );
        Vector retval = instance.evaluate( new Vector3( 0.0, 18.0, 0.0 ) );

        assertEquals( maxBufferSize, instance.getState().size() );

        Vector3 expected_ms = new Vector3( 0.0, 2.0, 0.0 );
        Vector3 expected_bs = new Vector3( 0.0, 18.0, 1.0 );
        Vector expected = expected_bs.stack(expected_ms);
//        Matrix expected = MatrixFactory.getDefault().createMatrix( 3, 2 );
//        expected.setColumn( 1, expected_ms );
//        expected.setColumn( 0, expected_bs );

        System.out.println( "Norm2: " + expected.minus( retval ).norm2() );
        if (!expected.equals( retval, 1e-5 ))
        {
            assertEquals( expected, retval );
        }

    }

}
