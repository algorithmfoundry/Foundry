/*
 * File:            SparseVectorTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.math.matrix.custom.SparseVector;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorTestHarness;

/**
 * Unit tests for class {@link SparseVector}.
 * 
 * @author  Justin Basilico
 */
public class SparseVectorTest
    extends VectorTestHarness
{

    /** 
     * Creates a new instance of SparseVectorTest
     * @param testName Name of the test
     */
    public SparseVectorTest(
        String testName)
    {
        super(testName);
    }
    
    @Override
    protected Vector createVector(
        final int numDim)
    {
        return new SparseVector(numDim);
    }

    @Override
    protected Vector createCopy(
        final Vector vector)
    {
        final SparseVector result = new SparseVector(vector.getDimensionality());
        vector.forEachNonZero((i, v) -> result.set(i, v));
        return result;
    }

    @Override
    public void testIsSparse()
    {
        assertTrue(this.createVector(10).isSparse());
    }
    
}
