/*
 * File:                SparseVectorFactoryTest.java
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

/**
 * Unit tests for SparseVectorFactoryTest.
 *
 * @author krdixon
 */
public class SparseVectorFactoryTest
    extends VectorFactoryTestHarness
{

    /**
     * Tests for class SparseVectorFactoryTest.
     * @param testName Name of the test.
     */
    public SparseVectorFactoryTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public SparseVectorFactory createFactory()
    {
        return SparseVectorFactory.getDefault();
    }

    /**
     * Test of getDefault method, of class SparseVectorFactory.
     */
    public void testGetDefaultSparse()
    {
        System.out.println("getDefault");
        SparseVectorFactory result = SparseVectorFactory.getDefault();
        assertNotNull( result );
        assertSame( SparseVectorFactory.DEFAULT_SPARSE_INSTANCE, result );
    }

    /**
     * Tests createVectorCapacity()
     */
    public void testCreateVectorCapacity()
    {
        System.out.println( "createVectorCapacity" );

        SparseVectorFactory factory = this.createFactory();
        int dim = RANDOM.nextInt(10) + 1;
        int capacity = dim-1;
        Vector v = factory.createVector(dim,capacity);
        assertNotNull( v );
        assertEquals( dim, v.getDimensionality() );

        Vector v2 = factory.createVector(dim,dim+1);
        assertNotNull( v2 );
        assertEquals( dim, v2.getDimensionality() );

    }

}
