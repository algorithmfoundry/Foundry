/*
 * File:                Token.java
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

package gov.sandia.cognition.text.token;

import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.TermOccurrence;

/**
 * Interface for a meaningful chunk of text, called a token.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Token
    extends TermOccurrence
{

    /**
     * Gets the text of the token.
     *
     * @return
     *      The text of the token.
     */
    public String getText();

    /**
     * Tokens always are treated as {@code DefaultTerm} objects that contain
     * the text of the {@code Token}.
     *
     * @return
     *      A {@code DefaultTerm} containing the text of the token.
     */
    public DefaultTerm asTerm();

}
