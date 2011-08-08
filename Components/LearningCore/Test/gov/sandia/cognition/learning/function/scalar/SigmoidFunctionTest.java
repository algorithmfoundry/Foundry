/*
 * File:                SigmoidFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import junit.framework.TestCase;

/**
 *
 * @author jdbasil
 */
public class SigmoidFunctionTest extends TestCase
{

    public SigmoidFunctionTest(String testName)
    {
        super(testName);
    }

    public void testClone()
    {
        System.out.println( "Clone" );

        SigmoidFunction f1 = new SigmoidFunction();
        SigmoidFunction clone = f1.clone();
        assertNotNull( clone );
        assertNotSame( f1, clone );
    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.SigmoidFunction.
     */
    public void testEvaluateScalar()
    {
        System.out.println("evaluate (Scalar)");

        double EPSILON = 1e-5;

        SigmoidFunction f1 = new SigmoidFunction();

        assertEquals(0.5, f1.evaluate(new Double(0.0)), EPSILON);

        double x, y;

        x = 1e10;
        y = 1.0;
        assertEquals(y, f1.evaluate(x), EPSILON);
        assertEquals(1.0 - y, f1.evaluate(-x), EPSILON);

        x = 1.0;
        y = 0.73106;
        assertEquals(y, f1.evaluate(x), EPSILON);
        assertEquals(1.0 - y, f1.evaluate(-x), EPSILON);

        x = 0.1;
        y = 0.52498;
        assertEquals(y, f1.evaluate(x), EPSILON);
        assertEquals(1.0 - y, f1.evaluate(-x), EPSILON);

    }

}
