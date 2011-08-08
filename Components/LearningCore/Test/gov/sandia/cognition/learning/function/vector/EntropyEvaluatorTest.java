/*
 * File:                EntropyEvaluatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for EntropyEvaluatorTest.
 *
 * @author krdixon
 */
public class EntropyEvaluatorTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Tests for class EntropyEvaluatorTest.
     * @param testName Name of the test.
     */
    public EntropyEvaluatorTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class EntropyEvaluatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        EntropyEvaluator f = new EntropyEvaluator();
        assertNotNull( f );
    }

    /**
     * Test of evaluate method, of class EntropyEvaluator.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        Vector input = VectorFactory.getDefault().copyValues( 0.2, 0.6, 0.2 );
        EntropyEvaluator f = new EntropyEvaluator();
        assertEquals( 1.370951, f.evaluate(input), EPS );


        input.setElement(0, 1.0);
        try
        {
            f.evaluate(input);
            fail( "Values must add to 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
