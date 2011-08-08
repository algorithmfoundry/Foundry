/*
 * File:                CSVParseException.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 2, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;

/**
 * The <code>CSVParseException</code> class implements an exception that is
 * thrown while parsing a CSV file.
 *
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Minor cosmetic changes. Otherwise, looks fine."
        ),    
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=false,
            comments="Looks fine"
        )
    }
)
public class CSVParseException
    extends Exception
{

    /**
     * Creates a new instance of <code>CSVParseException</code> without 
     * detail message.
     */
    public CSVParseException()
    {
        super();
    }

    /**
     * Constructs an instance of <code>CSVParseException</code> with the 
     * specified detail message.
     *
     * @param message The detail message.
     */
    public CSVParseException(
        String message )
    {
        super( message );
    }

    /**
     * Constructs an instance of <code>CSVParseException</code> with the 
     * given cause of the exception.
     *
     * @param cause The cause of the exception.
     */
    public CSVParseException(
        final Throwable cause )
    {
        super( cause );
    }

    /**
     * Constructs an instance of <code>CSVParseException</code> with the 
     * given message and cause of the exception.
     *
     * @param message The detailed message.
     * @param cause The cause of the exception.
     */
    public CSVParseException(
        final String message,
        final Throwable cause )
    {
        super( message, cause );
    }

}
