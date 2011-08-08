/*
 * File:                SemanticIdentifier.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import java.io.Serializable;

/**
 * The SemanticIdentifier class holds a SemanticLabel along with the unique
 * integer that can be used to identify the SemanticLabel within a model. It
 * is used so that dealing with SemanticLabels can be done really quickly by
 * using an integer rather than using a String or more complex data structure.
 * This is especially important in the hashCode and equals functions.
 * 
 * Only developers implementing a new Cognitive Model type would should be 
 * concerned with implementing this interface. Developers using a model or
 * developing a module should create SemanticIdentifiers through the model's
 * SemanticIdentifierMap.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface SemanticIdentifier
    extends Serializable, Comparable<SemanticIdentifier>
{
    /**
     * Computes the hash-code for the SemanticIdentifier, which should be
     * equal to a call to getIdentifier.
     *
     * @return The hash code of the identifier, which is the identifier number.
     */
    public int hashCode();

    /**
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
    public int compareTo(
        SemanticIdentifier o);
    
    /**
     * Determines if this identifier is equal to another Object. If that
     * Object is a SemanticIdentifier then the two are equal if the identifier
     * numbers are the same.
     *
     * @param  other The Object to test equality with.
     * @return True if this is equal to the given Object.
     */
    public boolean equals(
        Object other);
    
    /**
     * Determines if this identifier is equal to the given one by comparing
     * the identifier number only. This comparison is invalid if the two 
     * identifiers come from different models.
     *
     * @param other The other SemanticIdentifier to compare to.
     * @return True if the two semantic identifiers are equal.
     */
    public boolean equals(
        SemanticIdentifier other);

    /**
     * Gets the SemanticLabel.
     *
     * @return The SemanticLabel
     */
    public SemanticLabel getLabel();
    
    /**
     * Gets the integer identifier for the SemanticLabel
     *
     * @return The unique identifier for the label
     */
    public int getIdentifier();
}
