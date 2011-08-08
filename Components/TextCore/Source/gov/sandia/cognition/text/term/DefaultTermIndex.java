/*
 * File:                DefaultTermIndex.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A default implementation of the {@code TermIndex} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermIndex
    extends AbstractTermIndex
{

    /** The mapping of terms to their corresponding (unique) indices. */
    protected Map<Term, DefaultIndexedTerm> termMap;

    /** The list of terms ordered by indices. */
    protected List<DefaultIndexedTerm> termList;

    /**
     * Creates a new, empty {@code DefaultTermIndex}.
     */
    public DefaultTermIndex()
    {
        super();

        this.setTermMap(new HashMap<Term, DefaultIndexedTerm>());
        this.setTermList(new ArrayList<DefaultIndexedTerm>());
    }

    @Override
    public DefaultTermIndex clone()
    {
        final DefaultTermIndex clone = (DefaultTermIndex) super.clone();
        clone.termMap = new HashMap<Term, DefaultIndexedTerm>(this.termMap);
        clone.termList = new ArrayList<DefaultIndexedTerm>(this.termList);
        return clone;
    }

    public IndexedTerm add(
        final Term term)
    {
        if (term == null)
        {
            // Don't add a null term.
            return null;
        }
        
        // Get the current index of the term.
        DefaultIndexedTerm indexedTerm = this.getIndexedTerm(term);
        if (indexedTerm == null)
        {
            // The term is not in the index, so add it.
            int index = termList.size();
            indexedTerm = new DefaultIndexedTerm(index, term);
            this.termMap.put(term, indexedTerm);
            this.termList.add(indexedTerm);
        }

        // Return the index for the term.
        return indexedTerm;
    }


    public int getTermCount()
    {
        return this.termList.size();
    }

    public List<DefaultIndexedTerm> getTerms()
    {
        return Collections.unmodifiableList(this.getTermList());
    }

    public DefaultIndexedTerm getIndexedTerm(
        final Term term)
    {
        return this.termMap.get(term);
    }

    public DefaultIndexedTerm getIndexedTerm(
        final int index)
    {
        if (index < 0 || index >= this.getTermCount())
        {
            // Bad index.
            return null;
        }
        else
        {
            // Get the index of the term.
            return this.termList.get(index);
        }
    }

    /**
     * Gets the mapping of terms to their indices.
     *
     * @return 
     *      The mapping of terms to their indices.
     */
    protected Map<Term, DefaultIndexedTerm> getTermMap()
    {
        return this.termMap;
    }

    /**
     * Sets the mapping of terms to their indices.
     *
     * @param   termMap
     *      The mapping of terms to their indices.
     */
    protected void setTermMap(
        final Map<Term, DefaultIndexedTerm> termMap)
    {
        this.termMap = termMap;
    }

    /**
     * Sets the list of terms, ordered by index.
     *
     * @return 
     *      The list of terms, ordered by index.
     */
    protected List<DefaultIndexedTerm> getTermList()
    {
        return this.termList;
    }

    /**
     * Gets the list of terms, ordered by index.
     *
     * @param   termList
     *      The list of terms, ordered by index.
     */
    protected void setTermList(
        final List<DefaultIndexedTerm> termList)
    {
        this.termList = termList;
    }

}
