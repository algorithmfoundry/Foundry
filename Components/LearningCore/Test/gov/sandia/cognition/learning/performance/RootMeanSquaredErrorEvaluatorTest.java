/*
 * File:                RootMeanSquaredErrorEvaluatorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 18, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.learning.data.TargetEstimatePair;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;

/**
 * Tests of RootMeanSquaredErrorEvaluator
 *
 * @author  Justin Basilico
 * @since   2.0
 */
public class RootMeanSquaredErrorEvaluatorTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public RootMeanSquaredErrorEvaluatorTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class RootMeanSquaredErrorEvaluator.
     */
    public void testConstructors()
    {
        RootMeanSquaredErrorEvaluator<Object> instance =
            new RootMeanSquaredErrorEvaluator<Object>();
        assertNotNull( instance );
    }

    /**
     * Test of summarize method, of class RootMeanSquaredErrorEvaluator.
     */
    public void testSummarize()
    {
        RootMeanSquaredErrorEvaluator<Object> instance = new RootMeanSquaredErrorEvaluator<Object>();
        Collection<TargetEstimatePair<Double, Double>> data =
            new ArrayList<TargetEstimatePair<Double, Double>>();

        double error = 0.0;
        assertEquals(Math.sqrt(error), instance.summarize(data));

        data.add(new TargetEstimatePair<Double, Double>(4.7, 6.7));
        error += 2.0 * 2.0;
        assertEquals(Math.sqrt(error / 1.0), instance.summarize(data));

        data.add(new TargetEstimatePair<Double, Double>(4.7, 1.7));
        error += 3.0 * 3.0;
        assertEquals(Math.sqrt(error / 2.0), instance.summarize(data));

        data.add(new TargetEstimatePair<Double, Double>(7.0, 7.0));
        assertEquals(Math.sqrt(error / 3.0), instance.summarize(data));

        data.add(new TargetEstimatePair<Double, Double>(4.0, 4.0));
        assertEquals(Math.sqrt(error / 4.0), instance.summarize(data));

        data.add(new TargetEstimatePair<Double, Double>(10.0, 9.5));
        error += 0.5 * 0.5;
        assertEquals(Math.sqrt(error / 5.0), instance.summarize(data));

        boolean exceptionThrown = false;
        try
        {
            data.add(new TargetEstimatePair<Double, Double>(null, 4.0));
            instance.summarize(data);
        }
        catch (NullPointerException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}
