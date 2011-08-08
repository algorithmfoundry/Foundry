/*
 * File:                LowerCaseTermFilter.java
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

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.TermOccurrence;

/**
 * A term filter that converts all terms to lower case.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class LowerCaseTermFilter
    extends AbstractSingleTermFilter
{

    /**
     * Creates a new {@code LowerCaseTermFilter}.
     */
    public LowerCaseTermFilter()
    {
        super();
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurrence)
    {
        // Get the old text and convert it to lower case.
        final String newText =
            occurrence.getTerm().getName().toLowerCase();

        // Create the new term and add it to the result.
        final DefaultTerm newTerm = new DefaultTerm(newText);
        final DefaultTermOccurrence newOccurrence = new DefaultTermOccurrence(
            newTerm, occurrence.getStart(), occurrence.getLength());
        return newOccurrence;
    }

}
