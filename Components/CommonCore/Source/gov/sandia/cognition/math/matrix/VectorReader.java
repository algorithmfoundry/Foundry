/*
 * File:                VectorReader.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.io.ReaderTokenizer;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Reads a Vector from a single line in a file
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-22",
    changesNeeded=true,
    comments="Changes marked by triple slash",
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2006-07-20",
        moreChangesNeeded=false,
        comments="Changes from J.T.'s code review"
    )
)
public class VectorReader
{
// TODO: This class should have a field that is the vector factory to use.
// - jdbasil (2009-10-28)

    /** The start of a comment line has the "#" string at the beginning. */
    public static final String COMMENT_LINE_PREFIX = "#";
    
    /**
     * Parses the input stream and pulls out tokens
     */
    private ReaderTokenizer tokenizer;

    /**
     * Creates a new instance of VectorReader
     * 
     * @param reader Java-based reader from which to pull the Vector
     */
    public VectorReader(
        Reader reader )
    {
        this( new ReaderTokenizer( reader ) );
    }


    /**
     * Creates a new instance of VectorReader
     * 
     * 
     * @param tokenizer 
     * Parses the input stream and pulls out tokens
     */
    public VectorReader(
        ReaderTokenizer tokenizer )
    {
        this.setTokenizer( tokenizer );
    }
    
    /**
     * Reads a Vector off of the next line in the file
     * 
     * @return Vector that is on the next line in the file, or null if no
     * values were found
     * @throws java.io.IOException on a bad readLine on the BufferedReader
     */
    public Vector read()
        throws IOException
    {
        
        ArrayList<String> tokens = this.getTokenizer().readNextLine();
        Vector retval;
        if( tokens != null )
        {
            ArrayList<Double> values = new ArrayList<Double>( tokens.size() );
            for( String token : tokens )
            {
                values.add( Double.valueOf( token ) );
            }

            int M = values.size();
            if( M > 0 )
            {
                retval = VectorFactory.getDefault().createVector( M );
                for( int i = 0; i < M; i++ )
                {
                    retval.setElement( i, values.get(i) );
                }
            }
            else
            {
                retval = null;
            }
        }
        else
        {
            retval = null;
        }
        
        return retval;
    }

    /**
     * Parses a {@code Vector} from the given list of element tokens.
     * 
     * @param   tokens The collection of element tokens.
     * @return  A new {@code Vector} created from the given tokens.
     */
    public static Vector parseVector(
        final Collection<String> tokens)
    {
        if (tokens == null)
        {
            return null;
        }

        final int dimensionality = tokens.size();
        if (dimensionality <= 0)
        {
            return null;
        }

        Vector vector = VectorFactory.getDefault().createVector(dimensionality);
        int i = 0;
        for (String token : tokens)
        {
            final double value = Double.valueOf(token);
            vector.setElement(i, value);
            i++;
        }

        return vector;
    }
    
    
    /**
     * Reads a collection of Vectors from the Reader
     * 
     * @return List of Vectors, where the nth Vector was the nth
     * row in the Reader
     * @param mustBeSameSize true if the Vectors must be same size,
     * false allows any sized Vectors in the List
     * @throws java.io.IOException if the Reader isn't valid or doesn't contain 
     * a Vector
     */
    public List<Vector> readCollection(
        boolean mustBeSameSize )
        throws IOException
    {
        
        LinkedList<Vector> vectors = new LinkedList<Vector>();

        ArrayList<String> tokens = null;
        int N = -1;
        while ( (tokens = this.getTokenizer().readNextLine()) != null )
        {
            if ( tokens.size() > 0 )
            {
                String first = tokens.get(0).trim();
                if ( !first.startsWith(COMMENT_LINE_PREFIX) )
                {
                    
                    Vector rowVector = VectorReader.parseVector(tokens);

                    if ( rowVector != null )
                    {

                        vectors.add( rowVector );

                        if( mustBeSameSize == true )
                        {
                            if( N < 0 )
                            {
                                N = rowVector.getDimensionality();
                            }
                            else if( N != rowVector.getDimensionality() )
                            {
                                throw new IOException( "Expected " + N 
                                    + " elements in row " 
                                    + (vectors.size()+1) );
                            }
                        }
                    }
                }
            }
        }
        
        return vectors;
    }

    
    /**
     * Getter for tokenizer
     * @return 
     * Parses the input stream and pulls out tokens
     */
    protected ReaderTokenizer getTokenizer()
    {
        return this.tokenizer;
    }

    /**
     * Setter for tokenizer
     * @param tokenizer 
     * Parses the input stream and pulls out tokens
     */
    protected void setTokenizer(
        ReaderTokenizer tokenizer)
    {
        this.tokenizer = tokenizer;
    }
    
}
