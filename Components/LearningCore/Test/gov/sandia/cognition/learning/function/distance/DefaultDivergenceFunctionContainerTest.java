/*
 * File:                DefaultDivergenceFunctionContainerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 25, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.matrix.Vector;
import junit.framework.TestCase;

/**
 * Tests of DefaultDivergenceFunctionContainer
 *
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultDivergenceFunctionContainerTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public DefaultDivergenceFunctionContainerTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        EuclideanDistanceMetric divergenceFunction = null;
        DefaultDivergenceFunctionContainer<Vector, Vector> instance =
            new DefaultDivergenceFunctionContainer<Vector, Vector>();
        assertSame(divergenceFunction, instance.getDivergenceFunction());

        divergenceFunction = EuclideanDistanceMetric.INSTANCE;
        instance = new DefaultDivergenceFunctionContainer<Vector, Vector>(
            divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        
        DefaultDivergenceFunctionContainer<Vector, Vector> copy =
            new DefaultDivergenceFunctionContainer<Vector, Vector>(instance);
        assertNotNull(copy.getDivergenceFunction());
        assertNotSame(divergenceFunction, copy.getDivergenceFunction());
    }

    public void testClone()
    {
        System.out.println( "Clone" );
        DefaultDivergenceFunctionContainer<Vector, Vector> instance =
            new DefaultDivergenceFunctionContainer<Vector, Vector>(
                EuclideanDistanceMetric.INSTANCE );
        DefaultDivergenceFunctionContainer<Vector, Vector> clone = instance.clone();
        assertNotNull( clone );
        assertNotNull( clone.getDivergenceFunction() );
        assertNotSame( instance.getDivergenceFunction(), clone.getDivergenceFunction() );
    }

    /**
     * Test of getDivergenceFunction method, of class DefaultDivergenceFunctionContainer.
     */
    public void testGetDivergenceFunction()
    {
        this.testSetDivergenceFunction();
    }

    /**
     * Test of setDivergenceFunction method, of class DefaultDivergenceFunctionContainer.
     */
    public void testSetDivergenceFunction()
    {
        EuclideanDistanceMetric divergenceFunction = null;
        DefaultDivergenceFunctionContainer<Vector, Vector> instance =
            new DefaultDivergenceFunctionContainer<Vector, Vector>();
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        
        divergenceFunction = EuclideanDistanceMetric.INSTANCE;
        instance.setDivergenceFunction(divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
        
        divergenceFunction = null;
        instance.setDivergenceFunction(divergenceFunction);
        assertSame(divergenceFunction, instance.getDivergenceFunction());
    }

}
