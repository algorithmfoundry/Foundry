/*
 * File:                MatrixReader.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Reads a Matrix from the specified reader.  The format is an (MxN) array
 * of doubles, WITHOUT specifying the dimensions explicitly.  We determine the
 * matrix dimensions by first reading all the rows (Vectors) of the file,
 * then ensuring that each row (Vector) has the same dimension.
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-19",
    changesNeeded=false,
    comments="Looks fine."
)
public class MatrixReader
{

    /** internal reader that reads a vector of numbers off of each line */
    private VectorReader internalReader = null;
    
    /**
     * Creates a new instance of MatrixReader
     * 
     * 
     * @param reader Java reader stream from which to pull the data
     */
    public MatrixReader( 
        Reader reader )
    {
        this.setInternalReader( new VectorReader( reader ) );
    }

    /**
     * Getter for internalReader
     * @return internal reader that reads a vector of numbers off of each line
     */
    protected VectorReader getInternalReader()
    {
        return this.internalReader;
    }

    /**
     * Setter for internalReader
     * @param internalReader internal reader that reads a vector of numbers 
     * off of each line
     */
    protected void setInternalReader(
        VectorReader internalReader)
    {
        this.internalReader = internalReader;
    }


    /**
     * Reads the next Matrix found in the specified Reader
     * @throws java.io.IOException If an invalid reader is given, or no Matrix is found
     * @return the next Matrix found in the specified Reader
     */
    public Matrix read() throws IOException
    {
        
        /*
         * Read the collection of vectors from the file, but check to ensure
         * that they are all the same dimension
         */
        boolean mustBeSameSize = true;
        List<Vector> rowVectors = 
            this.getInternalReader().readCollection( mustBeSameSize );
        int N = -1;
        int index = 0;
        for( Vector r : rowVectors )
        {
            int numColumns = r.getDimensionality();
            if( N < 0 )
            {
                N = numColumns;
            }
            else if( N != numColumns )
            {
                throw new IOException( 
                    "Row " + index + 
                    " has different dimension than previous rows!" );
            }
            index++;
        }
        
        /*
         * If I've got zero rows in my Collection, then return null, otherwise
         * we're going to set each of the previously read-in vectors as the
         * rows of the matrix.
         */
        int M = rowVectors.size();
        Matrix retval;
        if( M <= 0 )
        {
            retval = null;
        }
        else
        {
            retval = MatrixFactory.getDefault().createMatrix( M, N );
            int rowIndex = 0;
            for( Vector r : rowVectors )
            {
                retval.setRow( rowIndex, r );
                rowIndex++;
            }
        }
        
        return retval;
        
    }
}
