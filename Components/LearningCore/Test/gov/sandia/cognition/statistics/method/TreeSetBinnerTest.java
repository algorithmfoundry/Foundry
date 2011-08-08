/*
 * File:                TreeSetBinnerTest.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 27, 2007, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.CodeReview;
import junit.framework.TestCase;

/**
 * Unit tests for class TreeSetBinner.
 *
 * @author  Zachary Benz
 * @since   2.0
 */
@CodeReview(
    reviewer = "Justin Basilico",
    date = "2009-05-29",
    changesNeeded = false,
    comments = "Cleaned up the formatting and javadoc."
)
public class TreeSetBinnerTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName
     *      The test name.
     */
    public TreeSetBinnerTest(
        final String testName)
    {
        super(testName);
    }

    /**
     * Test of findBin method, of class TreeSetBinner.
     */
    public void testfindBin()
    {
        TreeSetBinner<Double> binner =
            new TreeSetBinner<Double>(0.0, 3.0, 8.0, 7.0, 1.0);

        Double result = binner.findBin(-1.0);
        assertEquals(null, result);

        result = binner.findBin(0.0);
        assertEquals(0.0, result);

        result = binner.findBin(0.5);
        assertEquals(0.0, result);

        result = binner.findBin(1.0);
        assertEquals(1.0, result);

        result = binner.findBin(2.3);
        assertEquals(1.0, result);

        result = binner.findBin(3.0);
        assertEquals(3.0, result);

        result = binner.findBin(6.999);
        assertEquals(3.0, result);

        result = binner.findBin(7.0);
        assertEquals(7.0, result);

        result = binner.findBin(7.001);
        assertEquals(7.0, result);

        result = binner.findBin(8.0);
        assertEquals(null, result);

        result = binner.findBin(1000.0);
        assertEquals(null, result);
    }

    /**
     * Test of getBinCount method, of class TreeSetBinner.
     */
    public void testGetBinCount()
    {
        TreeSetBinner<Double> binner =
            new TreeSetBinner<Double>(0.0, 3.0, 8.0, 7.0, 1.0);
        assertEquals(4, binner.getBinCount());
    }

}
