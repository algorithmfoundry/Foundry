/*
 * File:                AbstractMatrixTest.java
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
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import java.util.Random;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AbstractMatrix
 *
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    changesNeeded=true,
    date="2006-05-17",
    comments={
        "Added proper header file.",
        "Added some documentation.",
        "Notes marked with  \"///\"."
    },
    response=@CodeReviewResponse(
        respondent="Kevin R Dixon",
        date="2006-05-18",
        comments="Added comment describing the questionable test.",
        moreChangesNeeded=false
    )
)
public class AbstractMatrixTest
    extends MatrixTestHarness
{
    /** 
     * Creates a new instance of AbstractMatrixTest.
     * @param testName
     */
    public AbstractMatrixTest(
        String testName)
    {
        super(testName);
    }

    protected Matrix createCopy(
        Matrix matrix)
    {
        if( (new Random()).nextBoolean() )
        {
            return DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( matrix );
        }
        else
        {
            return SparseMatrixFactoryMTJ.INSTANCE.copyMatrix( matrix );
        }
    }

    protected Matrix createMatrix(
        int numRows,
        int numColumns)
    {
        if( RANDOM.nextBoolean() )
        {
            return DenseMatrixFactoryMTJ.INSTANCE.createMatrix( numRows, numColumns );
        }
        else
        {
            return SparseMatrixFactoryMTJ.INSTANCE.createMatrix( numRows, numColumns );
        }
    }

}
