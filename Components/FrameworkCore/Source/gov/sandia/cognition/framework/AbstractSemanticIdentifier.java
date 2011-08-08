/*
 * File:                AbstractSemanticIdentifier.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The <code>AbstractSemanticIdentifier</code> class implements the basic
 * methods that are needed for a <code>SemanticIdentifier</code> to provide
 * a good speed improvement. In particular, it makes use of the getIdentifier
 * method to use the identifier in the hashCode and equals fuctions in order
 * to make them fast.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public abstract class AbstractSemanticIdentifier
    extends Object
    implements SemanticIdentifier
{
    /**
     * Creates a new instance of AbstractSemanticIdentifier
     */
    protected AbstractSemanticIdentifier()
    {
        super();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public @Override int hashCode()
    {
        return this.getIdentifier();
    }

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
    public int compareTo(
        SemanticIdentifier o)
    {
        return this.getIdentifier() - o.getIdentifier();
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  other {@inheritDoc}
     * @return {@inheritDoc}
     */
    public @Override boolean equals(
        Object other)
    {
        if ( other instanceof SemanticIdentifier )
        {
            // Compare the identifiers as objects.
            return this.equals((SemanticIdentifier) other);
        }
        else
        {
            // The given object is of another type so they cannot be equal.
            return false;
        }
    }
    
    /**
     * Determines if this identifier is equal to the given one by comparing
     * the identifier number only. This comparison is invalid if the two labels
     * come from different models.
     *
     * @param other The other SemanticIdentifier to compare to.
     * @return True if the two semantic identifiers are equal.
     */
    public boolean equals(
        SemanticIdentifier other)
    {
        if ( other == null )
        {
            // An object is never equal to null;
            return false;
        }
        else
        {
            // Compare the identifier.
            return (this.compareTo( other ) == 0);
        }
    }
}

