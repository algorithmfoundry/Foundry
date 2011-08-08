/*
 * File:                AbstractTerm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 01, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Creates a new {@code AbstractTerm}.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractTerm
    extends AbstractCloneableSerializable
    implements Term
{

    /**
     * Creates a new {@code AbstractTerm}.
     */
    public AbstractTerm()
    {
        super();
    }

    public final Term asTerm()
    {
        return this;
    }

    @Override
    public int hashCode()
    {
        // The hash code of the term is its name.
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof Term && this.equals((Term) other);
    }

    /**
     * Determines if this term is equal to another term. Two terms are equal if
     * they have the same name.
     *
     * @param   other
     *      The other term.
     * @return
     *      True if the two terms are equal. Otherwise, false.
     */
    public boolean equals(
        final Term other)
    {
        // Two terms are equal if they have the same name.
        return other != null && this.getName().equals(other.getName());
    }

    @Override
    public String toString()
    {
        return this.getName();
    }
    
}
