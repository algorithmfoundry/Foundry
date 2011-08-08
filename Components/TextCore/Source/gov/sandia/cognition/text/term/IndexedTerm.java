/*
 * File:                IndexedTerm.java
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

/**
 * Interface for a term plus its index.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface IndexedTerm
    extends Termable
{

    /**
     * Gets the term associated with the index.
     *
     * @return
     *      The term.
     */
    public Term getTerm();

    /**
     * Gets the index associated with the term.
     *
     * @return
     *      The index.
     */
    public int getIndex();

}
