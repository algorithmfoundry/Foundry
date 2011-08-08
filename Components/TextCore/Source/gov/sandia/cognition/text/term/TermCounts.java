/*
 * File:                TermCounts.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 02, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import java.util.Set;

/**
 * Interface for a class that holds counts associated with terms.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermCounts
{
    
    /**
     * Get the count for a given term.
     *
     * @param   term
     *      The term to get the counts for.
     * @return
     *      The counts for the given term, if it is known. Otherwise, 0.
     */
    public int getCount(
        final Term term);

    /**
     * Gets the total count of all terms. It is the sum of the count for all
     * terms that are known to this object.
     *
     * @return
     *      The total count of all terms.
     */
    public int getTotalCount();

    /**
     * Gets the set of all terms the counts are over.
     *
     * @return
     *      The set of terms the counts are over.
     */
    public Set<Term> getTerms();

}
