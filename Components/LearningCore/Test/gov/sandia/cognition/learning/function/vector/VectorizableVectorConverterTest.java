/*
 * File:                VectorizableVectorConverterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright December 3, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     VectorizableVectorConverter
 * 
 * @author Justin Basilico
 */
public class VectorizableVectorConverterTest
    extends TestCase
{

    /**
     * Creates a new unit test instance.
     * 
     * @param testName
     *      The test name.
     */
    public VectorizableVectorConverterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class VectorizableVectorConverter.
     */
    public void testConstructors()
    {
        VectorizableVectorConverter instance =
            new VectorizableVectorConverter();
        assertNotNull(instance);
    }

    /**
     * Test of clone method, of class VectorizableVectorConverter.
     */
    public void testClone()
    {
        VectorizableVectorConverter instance =
            new VectorizableVectorConverter();
        VectorizableVectorConverter clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(clone, instance);
    }

    /**
     * Test of evaluate method, of class VectorizableVectorConverter.
     */
    public void testEvaluate()
    {
        VectorizableVectorConverter instance =
            new VectorizableVectorConverter();
        Vector3 input = new Vector3(Math.random(), Math.random(),
            Math.random());
        assertSame(input, instance.evaluate(input));
    }

}
