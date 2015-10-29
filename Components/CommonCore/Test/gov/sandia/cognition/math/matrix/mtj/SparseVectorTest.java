/*
 * File:                SparseVectorTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 23, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorTestHarness;

/**
 * JUnit tests for class SparseVector
 * @author Kevin R. Dixon
 */
public class SparseVectorTest extends VectorTestHarness
{

    /**
     * Constructor
     * @param testName Name
     */
    public SparseVectorTest(
        String testName)
    {
        super(testName);
    }

    protected SparseVector createCopy(
        Vector vector)
    {
        return new SparseVector( vector );
    }

    protected SparseVector createVector(
        int numDim)
    {
        return new SparseVector( numDim );
    }

    @Override
    protected Vector createRandom()
    {
        Vector v = super.createRandom();
        if( v.getDimensionality() > 1 )
        {
            int index = RANDOM.nextInt(v.getDimensionality());
            v.setElement(index, 0.0);
            ((SparseVector) v).compact();
        }
        return v;
    }
    
    /**
     * Test of getNumNonZeroElements method, of class gov.sandia.isrc.math.matrix.mtj.SparseVector.
     */
    @SuppressWarnings("deprecation")
    public void testGetNumNonZeroElements()
    {
        System.out.println("getNumNonZeroElements");

        int index1 = 2;
        int index2 = 1;
        int index3 = 3;
        
        SparseVector instance = new SparseVector( 4 );
        
        assertEquals( 0, instance.getNumElementsUsed() );
        
        instance.setElement( index1, 2.718 );
        assertEquals( 1, instance.getNumElementsUsed() );
        
        instance.setElement( index2, -3.14 );
        assertEquals( 2, instance.getNumElementsUsed() );
        
        instance.setElement( index2, 0.0 );
        assertEquals( 2, instance.getNumElementsUsed() );
        
        instance.setElement( index2, 1.0 );
        assertEquals( 2, instance.getNumElementsUsed() );
        
        instance.setElement( index2, 0.0 );
        instance.setElement( index1, 0.0 );
        assertEquals( 2, instance.getNumElementsUsed() );
        
        instance.setElement( index3, 1.0 );
        assertEquals( 3, instance.getNumElementsUsed() );
        
    }

    /**
     *  Test of compact
     */
    public void testCompact()
    {
        System.out.println( "compact" );
        
        SparseVector v1 = (SparseVector) this.createRandom();
        
        v1.setElement( 0, 0.0 );
        v1.compact();
        
    }

    /**
     * Test of isSparse method.
     */
    public void testIsSparse()
    {
        // We expect isSparse to be consistent for a data structure.
        Vector v1 = this.createRandom();
        assertTrue(v1.isSparse());
        assertTrue(v1.isSparse());
        assertTrue(this.createRandom().isSparse());
        assertTrue(this.createVector(v1.getDimensionality()).isSparse());
    }
}
