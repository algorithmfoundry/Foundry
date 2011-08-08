/*
 * File:                KernelAdatronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.algorithm.perceptron.kernel.KernelAdatron;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: KernelAdatron
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelAdatronTest
    extends TestCase
{

    public KernelAdatronTest(
        String testName )
    {
        super( testName );
    }

    public void testConstants()
    {
        assertEquals( 100, KernelAdatron.DEFAULT_MAX_ITERATIONS );
    }

    public void testConstructors()
    {
        KernelAdatron<Vector> instance = new KernelAdatron<Vector>();
        assertNull( instance.getKernel() );
        assertEquals( KernelAdatron.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );

        PolynomialKernel kernel = new PolynomialKernel( 4, 7.0 );
        instance = new KernelAdatron<Vector>( kernel );
        assertSame( kernel, instance.getKernel() );
        assertEquals( KernelAdatron.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );

        int maxIterations = KernelAdatron.DEFAULT_MAX_ITERATIONS + 10;
        instance = new KernelAdatron<Vector>( kernel, maxIterations );
        assertSame( kernel, instance.getKernel() );
        assertEquals( maxIterations, instance.getMaxIterations() );
    }

    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        KernelAdatron instance = new KernelAdatron();

        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }


    public void testLearn()
    {
        KernelAdatron<Vector> instance = new KernelAdatron<Vector>(
            LinearKernel.getInstance(), 10000 );

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

        Evaluator<? super Vector,Boolean> result = instance.learn( examples );
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

        instance.setMaxIterations( 10000 );
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
     * Test of getKernel method, of class gov.sandia.cognition.learning.perceptron.KernelAdatron.
     */
    public void testGetKernel()
    {
        this.testSetKernel();
    }

    /**
     * Test of setKernel method, of class gov.sandia.cognition.learning.perceptron.KernelAdatron.
     */
    public void testSetKernel()
    {
        KernelAdatron<Vector> instance = new KernelAdatron<Vector>();
        assertNull( instance.getKernel() );

        Kernel<? super Vector> kernel = LinearKernel.getInstance();
        instance.setKernel( kernel );
        assertSame( kernel, instance.getKernel() );

        kernel = new PolynomialKernel( 4, 7.0 );
        instance.setKernel( kernel );
        assertSame( kernel, instance.getKernel() );

        instance.setKernel( null );
        assertNull( instance.getKernel() );
    }

    /**
     * Test of getResult method, of class gov.sandia.cognition.learning.perceptron.KernelAdatron.
     */
    public void testGetResult()
    {
    // Tested by learn.
    }

    /**
     * Test of getErrorCount method, of class gov.sandia.cognition.learning.perceptron.KernelAdatron.
     */
    public void testGetErrorCount()
    {
    // Tested by learn.
    }

}
