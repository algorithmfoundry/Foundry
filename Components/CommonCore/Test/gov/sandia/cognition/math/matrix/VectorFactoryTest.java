/*
 * File:                VectorFactoryTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for VectorFactoryTest.
 *
 * @author krdixon
 */
public class VectorFactoryTest
    extends VectorFactoryTestHarness
{

    /**
     * Tests for class VectorFactoryTest.
     * @param testName Name of the test.
     */
    public VectorFactoryTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public VectorFactory createFactory()
    {
        return VectorFactory.getDefault();
    }

    /**
     * Tests getDefault()
     */
    public void testGetDefault()
    {
        System.out.println("getDefault");
        VectorFactory f = VectorFactory.getDefault();
        assertNotNull(f);
        assertSame(f, VectorFactory.DEFAULT_DENSE_INSTANCE);
    }

    /**
     * Tests getDenseDefault()
     */
    public void testGetDenseDefault()
    {
        System.out.println("getDenseDefault");
        VectorFactory f = VectorFactory.getDenseDefault();
        assertNotNull(f);
        assertSame(VectorFactory.DEFAULT_DENSE_INSTANCE, f);
    }

    /**
     * Tests getSparseDefault()
     */
    public void testGetSparseDefault()
    {
        System.out.println("getSparseDefault");
        SparseVectorFactory f = VectorFactory.getSparseDefault();
        assertNotNull(f);
        assertSame(VectorFactory.DEFAULT_SPARSE_INSTANCE, f);
    }

    /**
     * Tests copyValues
     */
    public void testCreateVectorCollection()
    {

        List<Double> values = Arrays.asList(
            RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian() );
        Vector v = VectorFactory.getDefault().copyValues(values);
        assertEquals( values.size(), v.getDimensionality() );
        for( int i = 0; i < v.getDimensionality(); i++ )
        {
            assertEquals( values.get(i), v.getElement(i) );
        }

    }

}
