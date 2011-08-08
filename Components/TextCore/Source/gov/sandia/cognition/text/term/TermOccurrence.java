/*
 * File:                TermOccurrence.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import gov.sandia.cognition.text.OccurrenceInText;

/**
 * Interface for an occurrence of a term in some text.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermOccurrence
    extends OccurrenceInText<Term>, Termable
{

    /**
     * Gets the term that occurred in the text.
     *
     * @return
     *      The term that occurred in the text.
     */
    public Term getTerm();
    
}
