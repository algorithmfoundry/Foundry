/*
 * File:                DenseVectorTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 23, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.VectorTestHarness;
import junit.framework.*;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * JUnit tests for class DenseVector
 * @author Kevin R. Dixon
 */
public class DenseVectorTest extends VectorTestHarness
{
    
    public DenseVectorTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(DenseVectorTest.class);
        
        return suite;
    }

    protected DenseVector createCopy(Vector vector)
    {
        return new DenseVector( vector );
    }

    protected DenseVector createVector(int numDim)
    {
        return new DenseVector( numDim );
    }


    /**
     * Test of getArray method, of class gov.sandia.isrc.math.matrix.mtj.DenseVector.
     */
    public void testGetArray()
    {
        System.out.println("getArray");
        
        DenseVector v1 = (DenseVector) this.createRandom();
        
        double[] a1 = v1.getArray();
        
        assertEquals( v1.getDimensionality(), a1.length );
        
        for( int i = 0; i < v1.getDimensionality(); i++ )
        {
            assertEquals( a1[i], v1.getElement(i) );
        }
        
        
    }


    /**
     * Test of isSparse method.
     */
    public void testIsSparse()
    {
        // We expect isSparse to be consistent for a data structure.
        Vector v1 = this.createRandom();
        assertFalse(v1.isSparse());
        assertFalse(v1.isSparse());
        assertFalse(this.createRandom().isSparse());
        assertFalse(this.createVector(v1.getDimensionality()).isSparse());
    }
    
}
