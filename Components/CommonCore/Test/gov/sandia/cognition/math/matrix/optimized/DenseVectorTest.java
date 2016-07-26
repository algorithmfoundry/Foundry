/*
 * File:            DenseVectorTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.custom.DenseVector;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorTestHarness;

/**
 * Unit tests for class {@link DenseVector}.
 * 
 * @author  Justin Basilico
 */
public class DenseVectorTest
    extends VectorTestHarness
{

    /** 
     * Creates a new instance of DenseVectorTest
     * @param testName Name of the test
     */
    public DenseVectorTest(
        String testName)
    {
        super(testName);
    }
    
    @Override
    protected Vector createVector(
        final int numDim)
    {
        return new DenseVector(numDim);
    }

    @Override
    protected Vector createCopy(
        final Vector vector)
    {
        return new DenseVector(vector.toArray());
    }

    @Override
    public void testIsSparse()
    {
        assertFalse(this.createVector(10).isSparse());
    }
    
}
