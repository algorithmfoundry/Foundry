/*
 * File:                AbstractVectorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 17, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.mtj.DenseVector;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AbstractVector
 * 
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments={
        "Added proper file header.",
        "Added some documentation.",
        "Otherwise looks good."
    }
)
public class AbstractVectorTest
    extends VectorTestHarness
{
    
    protected Vector createCopy(
        Vector vector)
    {
        if( RANDOM.nextBoolean() )
        {
            return DenseVectorFactoryMTJ.INSTANCE.copyVector( vector );
        }
        else
        {
            return SparseVectorFactoryMTJ.INSTANCE.copyVector( vector );
        }
        
    }

    protected Vector createVector(
        int numDim)
    {
        
        if( RANDOM.nextBoolean() )
        {
            return DenseVectorFactoryMTJ.INSTANCE.createVector( numDim );
        }
        else
        {
            return SparseVectorFactoryMTJ.INSTANCE.createVector( numDim );
        }        
    }
    
    
    /** 
     * Creates a new instance of AbstractVectorTest.
     */
    public AbstractVectorTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of isSparse method.
     */
    public void testIsSparse()
    {
        // We expect isSparse to be consistent for a data structure.
        Vector v1 = this.createRandom();
        assertTrue(v1 instanceof DenseVector ^ v1.isSparse());
    }

}
