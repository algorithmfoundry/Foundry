/*
 * File:                DefaultIndexedTerm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Default implementation of the {@code IndexedTerm} interface. It just stores
 * the index and the term.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultIndexedTerm
    extends AbstractCloneableSerializable
    implements IndexedTerm
{

    /** The default index is {@value}. */
    public static final int DEFAULT_INDEX = -1;

    /** The index of the term. */
    protected int index;

    /** The term. */
    protected Term term;

    /**
     * Creates a new {@code DefaultIndexedTerm} with default values.
     */
    public DefaultIndexedTerm()
    {
        this(DEFAULT_INDEX, null);
    }

    /**
     * Creates a new {@code DefaultIndexedTerm} with the given index and term.
     *
     * @param   index
     *      The index of the term.
     * @param term
     *      The term.
     */
    public DefaultIndexedTerm(
        final int index,
        final Term term)
    {
        super();

        this.setIndex(index);
        this.setTerm(term);
    }

    @Override
    public DefaultIndexedTerm clone()
    {
        final DefaultIndexedTerm clone = (DefaultIndexedTerm) super.clone();
        clone.term = ObjectUtil.cloneSafe(this.term);
        return clone;
    }


    @Override
    public int hashCode()
    {
        // An auto-generated hashcode:
        int hash = 5;
        hash = 89 * hash + this.index;
        hash = 89 * hash + (this.term != null ? this.term.hashCode() : 0);
        return hash;
    }


    @Override
    public boolean equals(
        final Object other)
    {
        return (this == other)
            || (other instanceof DefaultIndexedTerm
                && this.equals((DefaultIndexedTerm) other));
    }

    /**
     * Determines if this object is equal to the given object. They are equal if
     * they have equal indices and terms.
     *
     * @param   other
     *      Another object.
     * @return
     *      True if the this is equal to the given object. Otherwise, false.
     */
    public boolean equals(
        final DefaultIndexedTerm other)
    {
        return other != null
            && this.index == other.index
            && ObjectUtil.equalsSafe(this.term, other.term);
    }

    public Term asTerm()
    {
        return this.getTerm();
    }

    public int getIndex()
    {
        return this.index;
    }

    /**
     * Sets the index of the term.
     *
     * @param   index
     *      The index of the term.
     */
    protected void setIndex(
        final int index)
    {
        this.index = index;
    }

    public Term getTerm()
    {
        return this.term;
    }

    /**
     * Sets the term.
     *
     * @param   term
     *      The term.
     */
    protected void setTerm(
        final Term term)
    {
        this.term = term;
    }

    @Override
    public String toString()
    {
        return "(" + this.index + ", " + this.term + ")";
    }


}
