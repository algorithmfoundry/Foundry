/*
 * File:                SparseMatrix.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 11, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.math.matrix.Matrix;

/**
 * A sparse matrix, represented as a collection of sparse row vectors. 
 * Generally, this is the fastest sparse matrix for premultiplying against
 * a vector. Adding elements to this matrix is fast. The matrix will
 * not automatically remove zeroed elements.  Based on SparseRowMatrix.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-27",
    changesNeeded=true,
    comments="The readObject and writeObjects need to be implemented or removed if they are no longer needed.",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2007-04-02",
        moreChangesNeeded=false,
        comments="Moved readObject and writeObject up the inheritance tree."
    )
)
public class SparseMatrix
    extends SparseRowMatrix
{

    /**
     * Creates a new empty instance of SparseMatrix.
     * 
     * @param numRows Number of rows in the matrix.
     * @param numColumns Number of columns in the matrix.
     */
    protected SparseMatrix(
        int numRows,
        int numColumns )
    {
        super( numRows, numColumns );
    }

    /**
     * Converts the given matrix to a SparseMatrix. 
     *
     * @param matrix Matrix to convert, will not be modified.
     */
    protected SparseMatrix(
        Matrix matrix )
    {
        super( matrix );
    }

}
