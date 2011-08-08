/*
 * File:                StopList.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.Termable;

/**
 * Interface for a list of stop words.
 * 
 * @author  Justin Basilico
 * @since   3.0
 * @see     StopListFilter
 */
public interface StopList
{

    /**
     * Determines if the given term is contained in this stop list.
     *
     * @param   term
     *      The term.
     * @return
     *      True if the term is in the list and false otherwise.
     */
    public boolean contains(
        final Termable term);

}
