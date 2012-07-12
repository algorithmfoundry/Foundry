/*
 * File:                PerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     Perceptron
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class PerceptronTest
    extends TestCase
{

    public PerceptronTest(
        String testName )
    {
        super( testName );
    }

    public void testConstnats()
    {
        assertEquals( 100, Perceptron.DEFAULT_MAX_ITERATIONS );
        assertEquals( 0.0, Perceptron.DEFAULT_MARGIN_POSITIVE );
        assertEquals( 0.0, Perceptron.DEFAULT_MARGIN_NEGATIVE );
    }

    public void testConstructors()
    {
        Perceptron instance = new Perceptron();
        assertEquals( Perceptron.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
        assertEquals( Perceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive() );
        assertEquals( Perceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative() );
        assertSame( VectorFactory.getDefault(), instance.getVectorFactory() );

        int maxIterations = Perceptron.DEFAULT_MAX_ITERATIONS + 10;
        instance = new Perceptron( maxIterations );
        assertEquals( maxIterations, instance.getMaxIterations() );
        assertEquals( Perceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive() );
        assertEquals( Perceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative() );
        assertSame( VectorFactory.getDefault(), instance.getVectorFactory() );

        double marginPositive = Math.random();
        double marginNegative = Math.random();
        instance = new Perceptron( maxIterations, marginPositive, marginNegative );
        assertEquals( maxIterations, instance.getMaxIterations() );
        assertEquals( marginPositive, instance.getMarginPositive() );
        assertEquals( marginNegative, instance.getMarginNegative() );
        assertSame( VectorFactory.getDefault(), instance.getVectorFactory() );

        VectorFactory<?> factory = new SparseVectorFactoryMTJ();
        instance = new Perceptron( maxIterations, marginPositive, marginNegative,
            factory );
        assertEquals( maxIterations, instance.getMaxIterations() );
        assertEquals( marginPositive, instance.getMarginPositive() );
        assertEquals( marginNegative, instance.getMarginNegative() );
        assertSame( factory, instance.getVectorFactory() );
    }

    public void testLearn()
    {
        Perceptron instance = new Perceptron();

        Vector2[] positives = new Vector2[]{
            new Vector2( 1.00, 1.00 ),
            new Vector2( 1.00, 3.00 ),
            new Vector2( 0.25, 4.00 ),
            new Vector2( 2.00, 1.00 ),
            new Vector2( 5.00, -3.00 )
        };

        Vector2[] negatives = new Vector2[]{
            new Vector2( 2.00, 3.00 ),
            new Vector2( 2.00, 4.00 ),
            new Vector2( 3.00, 2.00 ),
            new Vector2( 4.25, 3.75 ),
            new Vector2( 4.00, 7.00 ),
            new Vector2( 7.00, 4.00 )
        };

        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        for (Vector2 example : positives)
        {
            examples.add( new DefaultInputOutputPair<Vector2, Boolean>( example, true ) );
        }

        for (Vector2 example : negatives)
        {
            examples.add( new DefaultInputOutputPair<Vector2, Boolean>( example, false ) );
        }

        Evaluator<? super Vector2,Boolean> result = instance.learn( examples );
        assertEquals( 0, instance.getErrorCount() );
        assertEquals( result, instance.getResult() );

        for (Vector2 example : positives)
        {
            assertTrue( result.evaluate( example ) );
        }

        for (Vector2 example : negatives)
        {
            assertFalse( result.evaluate( example ) );
        }

        instance.setMargin( 10.0 );
        instance.setMaxIterations( 1000 );
        result = instance.learn( examples );
        assertEquals( 0, instance.getErrorCount() );
        assertEquals( result, instance.getResult() );


        for (Vector2 example : positives)
        {
            assertTrue( result.evaluate( example ) );
        }

        for (Vector2 example : negatives)
        {
            assertFalse( result.evaluate( example ) );
        }

        instance.setMaxIterations( instance.getIteration() / 2 );
        result = instance.learn( examples );
        assertTrue( instance.getErrorCount() > 0 );

        examples = new ArrayList<InputOutputPair<Vector2, Boolean>>();
        result = instance.learn( examples );
        assertNull( result );

        result = instance.learn( null );
        assertNull( result );
    }

    /**
     * Test of setMargin method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testSetMargin()
    {
        Perceptron instance = new Perceptron();
        double margin = Math.random();
        instance.setMargin( margin );
        assertEquals( margin, instance.getMarginPositive() );
        assertEquals( margin, instance.getMarginNegative() );
    }

    /**
     * Test of getMarginPositive method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testGetMarginPositive()
    {
        this.testSetMarginPositive();
    }

    /**
     * Test of setMarginPositive method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testSetMarginPositive()
    {
        Perceptron instance = new Perceptron();
        assertEquals( Perceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive() );

        double margin = Math.random();
        instance.setMarginPositive( margin );
        assertEquals( margin, instance.getMarginPositive() );

        margin = 0.0;
        instance.setMarginPositive( margin );
        assertEquals( margin, instance.getMarginPositive() );

        margin = -1.0;
        instance.setMarginPositive( margin );
        assertEquals( margin, instance.getMarginPositive() );
    }

    /**
     * Test of getMarginNegative method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testGetMarginNegative()
    {
        this.testSetMarginNegative();
    }

    /**
     * Test of setMarginNegative method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testSetMarginNegative()
    {
        Perceptron instance = new Perceptron();
        assertEquals( Perceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative() );

        double margin = Math.random();
        instance.setMarginNegative( margin );
        assertEquals( margin, instance.getMarginNegative() );

        margin = 0.0;
        instance.setMarginNegative( margin );
        assertEquals( margin, instance.getMarginNegative() );

        margin = -1.0;
        instance.setMarginNegative( margin );
        assertEquals( margin, instance.getMarginNegative() );
    }

    /**
     * Test of getVectorFactory method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testSetVectorFactory()
    {
        Perceptron instance = new Perceptron();
        assertSame( VectorFactory.getDefault(), instance.getVectorFactory() );

        VectorFactory<?> factory = new SparseVectorFactoryMTJ();
        instance.setVectorFactory( factory );
        assertSame( factory, instance.getVectorFactory() );

        instance.setVectorFactory( null );
        assertNull( instance.getVectorFactory() );
    }

    /**
     * Test of getResult method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testGetResult()
    {
    // Tested by learn.
    }

    /**
     * Test of getErrorCount method, of class gov.sandia.cognition.learning.perceptron.Perceptron.
     */
    public void testGetErrorCount()
    {
    // Tested by learn.
    }

}
