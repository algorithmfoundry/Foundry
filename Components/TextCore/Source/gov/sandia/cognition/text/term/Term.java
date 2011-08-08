/*
 * File:                Term.java
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

import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.Named;

/**
 * Interface for a term, which is a basic unit of data in information retrieval.
 * Typically the term is a word, but it can be other things such as phrases.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Term
    extends Named, Termable, CloneableSerializable
{
}
