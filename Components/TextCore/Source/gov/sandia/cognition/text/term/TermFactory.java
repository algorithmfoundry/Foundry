/*
 * File:                TermFactory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import gov.sandia.cognition.text.token.Token;

/**
 * A factory for {@code Term} objects.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermFactory
{

    /**
     * Creates a new term from the given token.
     *
     * @param   token
     *      The token from which to create the term.
     * @return
     *      The new term.
     */
    public Term createTerm(
        final Token token);

    /**
     * Creates a new term from the given string.
     *
     * @param   term
     *      The string from which to create the term.
     * @return
     *      The new term.
     */
    public Term createTerm(
        final String term);

}
