/*
 * File:                MatrixWriter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
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
import java.io.Writer;

/**
 * Writes a Matrix to a Java-based Writer (files, etc.)
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-19",
    changesNeeded=false,
    comments="Looks fine."
)
public class MatrixWriter
{
    
    /** Java-based writer to spew out strings */
    private Writer writer;    
    
    /**
     * Creates a new instance of MatrixWriter
     * 
     * @param writer Java-based writer to spew out strings
     */
    public MatrixWriter(
        Writer writer )
    {
        this.setWriter( writer );
    }

    /**
     * Writes the given matrix to the Java-based writer
     * @param matrix Matrix to write to the Java-based writer
     * @throws java.io.IOException on bad write
     */
    public void write(
        Matrix matrix )
        throws IOException
    {
        this.getWriter().write( matrix.toString() );
        this.getWriter().flush();
    }
    
    
    /**
     * Getter for writer
     * @return Java-based writer to spew out strings
     */
    public Writer getWriter()
    {
        return this.writer;
    }

    /**
     * Setter for writer
     * @param writer Java-based writer to spew out strings
     */
    public void setWriter(
        Writer writer)
    {
        this.writer = writer;
    }
    
}
