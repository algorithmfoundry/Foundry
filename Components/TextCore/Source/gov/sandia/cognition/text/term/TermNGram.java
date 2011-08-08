/*
 * File:                TermNGram.java
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

import java.util.List;

/**
 * Interface for a term that is some type of n-gram. An n-gram is a tuple
 * containing n terms, any of which may be null.
 *
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermNGram
    extends Term
{
    
    /**
     * Gets the number of terms in the n-gram.
     * 
     * @return
     *      The number of terms in the n-gram.
     */
    public int getTermCount();

    /**
     * Gets the i-th term in the n-gram.
     *
     * @param   i
     *      The zero-based index of the term to get.
     * @return
     *      The i-th term in the n-gram.
     */
    public Term getTerm(
        final int i);

    /**
     * Gets the list of terms in the n-gram.
     *
     * @return
     *      The list of terms in the n-gram.
     */
    public List<Term> getTermList();
    
}
