/*
 * File:                AbstractMTJVectorTest.java
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

import gov.sandia.cognition.math.matrix.VectorTestHarness;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;

/**
 * JUnit tests for class AbstractMTJVector
 * @author Kevin R. Dixon
 */
public class AbstractMTJVectorTest
    extends VectorTestHarness
{
    
    public AbstractMTJVectorTest(
        String testName)
    {
        super(testName);
    }

    protected AbstractMTJVector createVector(int numDim)
    {
        
        if( (new Random()).nextBoolean() )
        {
            return new DenseVector( numDim );
        }
        else
        {
            return new SparseVector( numDim );
        }
        
    }

    protected AbstractMTJVector createCopy(Vector vector)
    {
        
        if( (new Random()).nextBoolean() )
        {
            return new DenseVector( vector );
        }
        else
        {
            return new SparseVector( vector );
        }
        
    }

    /**
     * Test of getInternalVector method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJVector.
     */
    public void testGetInternalVector()
    {
        
        System.out.println("getInternalVector");
     
        AbstractMTJVector v1 = (AbstractMTJVector) this.createRandom();
        assertNotNull( v1.getInternalVector() );
        
    }

    /**
     * Test of setInternalVector method, of class gov.sandia.cognition.math.matrix.mtj.AbstractMTJVector.
     */
    public void testSetInternalVector()
    {
        System.out.println("setInternalVector");

        AbstractMTJVector v1 = (AbstractMTJVector) this.createRandom();
        int M = v1.getDimensionality();
        
        no.uib.cipr.matrix.Vector i1 = v1.getInternalVector();
        
        assertNotNull( i1 );
        
        AbstractMTJVector v2 = v1.clone();
        
        no.uib.cipr.matrix.Vector i2 = v2.getInternalVector();
        
        assertNotNull( i2 );
        
        assertEquals( i1, v1.getInternalVector() );
        v1.setInternalVector( i2 );
        assertEquals( i2, v1.getInternalVector() );
        
    }
    
    
}
