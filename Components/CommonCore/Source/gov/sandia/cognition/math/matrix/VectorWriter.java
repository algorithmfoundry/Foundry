/*
 * File:                VectorWriter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import java.io.IOException;
import java.io.Writer;

/**
 * Writes a Vector to a Java-based Writer (files, etc.)
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-22",
    changesNeeded=false,
    comments="Looks fine."
)
public class VectorWriter
{
    
    /** Java-based writer to spew out strings */
    private Writer writer;
    
    /**
     * Creates a new instance of VectorWriter
     * 
     * @param writer Java-based writer to spew out strings
     */
    public VectorWriter(
        Writer writer )
    {
        this.setWriter( writer );
    }

    /**
     * Writes the given vector to the Java-based writer
     * @param vector Vector to write to the Java-based writer
     * @throws java.io.IOException on bad write
     */
    public void write(
        Vector vector )
        throws IOException
    {
        this.getWriter().write( vector.toString() );
        this.getWriter().write( "\n" );
        this.getWriter().flush();
    }
    
    
    /**
     * Writes the collection of vectors to the Java-based writer
     * @param vectors Collection of Vectors to write
     * @throws java.io.IOException on bad write
     */
    public void writeCollection(
        Iterable<? extends Vector> vectors )
        throws IOException
    {
        
        for( Vector vector : vectors )
        {
            this.write( vector );
        }
        
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
