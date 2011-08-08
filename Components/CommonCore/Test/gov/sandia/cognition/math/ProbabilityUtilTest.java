/*
 * File:                ProbabilityUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import junit.framework.TestCase;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class ProbabilityUtilTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ProbabilityUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of assertIsProbability method, of class ProbabilityUtil.
     */

    public void testAssertIsProbability()
    {
        ProbabilityUtil.assertIsProbability(0.5);
        ProbabilityUtil.assertIsProbability(0.0);
        ProbabilityUtil.assertIsProbability(1.0);

        boolean exceptionThrown = false;
        try
        {
            ProbabilityUtil.assertIsProbability(-1.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

        exceptionThrown = false;
        try
        {
            ProbabilityUtil.assertIsProbability(1.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }

    }

}
