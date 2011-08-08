/*
 * File:                AbstractTermIndex.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 08, 2010, Sandia Corporation.
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
 * An abstract implementation of the {@code TermIndex} class that handles a lot
 * of the convenience method implementations.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractTermIndex
    extends AbstractCloneableSerializable
    implements TermIndex
{

    /**
     * Creates a new {@code AbstractTermIndex}.
     */
    public AbstractTermIndex()
    {
        super();
    }

    public IndexedTerm getIndexedTerm(
        final Termable termable)
    {
        return this.getIndexedTerm(termable.asTerm());
    }

    public boolean hasIndexedTerm(
        final IndexedTerm indexedTerm)
    {
        return indexedTerm != null
            && this.hasIndex(indexedTerm.getIndex())
            && ObjectUtil.equalsSafe(indexedTerm.getTerm(),
                this.getTerm(indexedTerm.getIndex()));
    }

    public boolean hasTerm(
        final Termable termable)
    {
        return this.hasTerm(termable.asTerm());
    }

    public boolean hasTerm(
        final Term term)
    {
        // See if the term is in the term map.
        return this.getIndexedTerm(term) != null;
    }

    public boolean hasIndex(
        final int index)
    {
        return index >= 0 && index < this.getTermCount();
    }

    public int getIndex(
        final Termable termable)
    {
        return this.getIndex(termable.asTerm());
    }

    public int getIndex(
        final Term term)
    {
        // Get the index of the term.
        final IndexedTerm result = this.getIndexedTerm(term);

        if (result == null)
        {
            // Not in the map.
            return -1;
        }
        else
        {
            // In the map.
            return result.getIndex();
        }
    }

    public Term getTerm(
        final int index)
    {
        final IndexedTerm indexedTerm = this.getIndexedTerm(index);

        if (indexedTerm == null)
        {
            return null;
        }
        else
        {
            return indexedTerm.asTerm();
        }
    }

    public IndexedTerm add(
        final Termable termable)
    {
        return this.add(termable.asTerm());
    }

    public void addAll(
        final Iterable<? extends Termable> terms)
    {
        for (Termable termable : terms)
        {
            this.add(termable);
        }
    }
}
