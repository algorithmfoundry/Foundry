/*
 * File:                Termable.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

/**
 * Interface for an object that can be turned into a {@code Term}. It is used
 * so that things that can be treated like terms can be used with the different
 * objects and algorithms rather than having to explicitly convert to terms
 * beforehand. Note that a {@code Term} is by definition {@code Termable} and
 * should return itself.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Termable
{

    /**
     * Get the term for the object. This can be either a representation of the
     * object as a term or a term that is part of the object.
     *
     * @return
     *      The term representation of the object.
     */
    public Term asTerm();

}
