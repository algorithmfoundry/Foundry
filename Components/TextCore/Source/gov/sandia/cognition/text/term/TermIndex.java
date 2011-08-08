/*
 * File:                TermIndex.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import java.util.List;

/**
 * An interface for an index of terms. It assigns each term a unique
 * non-negative integer index.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermIndex
{

    /**
     * Gets the number of terms.
     *
     * @return
     *      The total number of terms.
     */
    public int getTermCount();

    /**
     * Gets all of the terms in the index.
     *
     * @return
     *      A list of all the terms in the index.
     */
    public List<? extends IndexedTerm> getTerms();

    /**
     * Gets the index-term pair for the given term, if it is in the index.
     * Otherwise, null is returned.
     *
     * @param   term
     *      The term to get the index-term pair for.
     * @return
     *      The index-term pair for the given term, if it is in the index.
     *      Otherwise, null.
     */
    public IndexedTerm getIndexedTerm(
        final Termable term);

    /**
     * Gets the index-term pair for the given term, if it is in the index.
     * Otherwise, null is returned.
     *
     * @param   term
     *      The term to get the index-term pair for.
     * @return
     *      The index-term pair for the given term, if it is in the index.
     *      Otherwise, null.
     */
    public IndexedTerm getIndexedTerm(
        final Term term);

    /**
     * Gets the index-term pair for the given index.
     *
     * @param   index
     *      The index to get the index-term pair for.
     * @return
     *      The index-term pair for the given index.
     */
    public IndexedTerm getIndexedTerm(
        final int index);

    /**
     * Determines if the given indexed term matches the term at the given index
     * value in this term index.
     * 
     * @param   indexedTerm
     *      An index-term pair.
     * @return
     *      True if the given index-term pair is an exact match for the term
     *      at the given index in this index. Otherwise, false.
     */
    public boolean hasIndexedTerm(
        final IndexedTerm indexedTerm);

    /**
     * Determines if the index contains the given term.
     *
     * @param   term
     *      The term.
     * @return
     *      True if the term is in the index and false otherwise.
     */
    public boolean hasTerm(
        final Termable term);

    /**
     * Determines if the index contains the given term.
     *
     * @param   term
     *      The term.
     * @return
     *      True if the term is in the index and false otherwise.
     */
    public boolean hasTerm(
        final Term term);

    /**
     * Determines if the term index contains the given index.
     *
     * @param   index
     *      An index.
     * @return
     *      True if if is a valid index. Otherwise, false.
     */
    public boolean hasIndex(
        final int index);

    /**
     * Gets the index of the given term. If it is not in the index, -1 is
     * returned.
     *
     * @param   term
     *      The term to get the index of.
     * @return
     *      The index of the term, if is is in the index; otherwise, -1.
     */
    public int getIndex(
        final Termable term);

    /**
     * Gets the index of the given term. If it is not in the index, -1 is
     * returned.
     *
     * @param   term
     *      The term to get the index of.
     * @return
     *      The index of the term, if is is in the index; otherwise, -1.
     */
    public int getIndex(
        final Term term);

    /**
     * Gets the term associated with the given index. If there is no term,
     * null is returned.
     *
     * @param   index
     *      The index to get the term for.
     * @return
     *      The term at the given index, if there is a term; otherwise, null.
     */
    public Term getTerm(
        final int index);

    /**
     * Adds the given term to the index. The returned value will be the index
     * of that term and if it is not in the index already, it will be added.
     *
     * @param   termable
     *      The term to add.
     * @return
     *      The index of the term.
     */
    public IndexedTerm add(
        final Termable termable);

    /**
     * Adds the given term to the index. The returned value will be the index
     * of that term and if it is not in the index already, it will be added.
     *
     * @param   term
     *      The term to add.
     * @return
     *      The index of the term.
     */
    public IndexedTerm add(
        final Term term);

    /**
     * Adds all of the given terms to the index, if they are not already part
     * of it.
     *
     * @param   terms
     *      The terms to add to the index.
     */
    public void addAll(
        final Iterable<? extends Termable> terms);

}
