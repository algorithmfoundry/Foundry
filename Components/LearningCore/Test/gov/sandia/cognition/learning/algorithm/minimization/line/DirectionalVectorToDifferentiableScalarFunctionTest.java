/*
 * File:                DirectionalVectorToDifferentiableScalarFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.math.matrix.Vector;

/**
 * Unit tests for DirectionalVectorToDifferentiableScalarFunctionTest.
 *
 * @author krdixon
 */
public class DirectionalVectorToDifferentiableScalarFunctionTest
    extends DirectionalVectorToScalarFunctionTest
{

    /**
     * Tests for class DirectionalVectorToDifferentiableScalarFunctionTest.
     * @param testName Name of the test.
     */
    public DirectionalVectorToDifferentiableScalarFunctionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public DirectionalVectorToDifferentiableScalarFunction createInstance()
    {
        Vector point = this.createRandomInput();
        Vector direction = this.createRandomInput();
        return new DirectionalVectorToDifferentiableScalarFunction(
            new TestVectorScalarFunction(), point, direction );
    }

    /**
     * Test of getLastGradient method, of class DirectionalVectorToDifferentiableScalarFunction.
     */
    public void testGetLastGradient()
    {
        System.out.println("getLastGradient");
        DirectionalVectorToDifferentiableScalarFunction instance = this.createInstance();
        assertNull( instance.getLastGradient() );
        instance.differentiate( random.nextGaussian() );
        assertNotNull( instance.getLastGradient() );
    }

    /**
     * Test of setLastGradient method, of class DirectionalVectorToDifferentiableScalarFunction.
     */
    public void testSetLastGradient()
    {
        System.out.println("setLastGradient");
        DirectionalVectorToDifferentiableScalarFunction instance = this.createInstance();
        assertNull( instance.getLastGradient() );
        instance.differentiate( random.nextGaussian() );
        assertNotNull( instance.getLastGradient() );
        instance.setLastGradient(null);
        assertNull( instance.getLastGradient() );
    }



}
