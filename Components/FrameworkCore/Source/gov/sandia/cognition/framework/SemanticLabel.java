/*
 * File:                SemanticLabel.java
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
 * This interface defines what a semantic label should have.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface SemanticLabel
    extends Serializable
{
    /**
     * Gets the human-readable name of the label.
     *
     * @return The name of the label
     */
    public String getName();
    
    /**
     * Computes the hash-code for the label.
     *
     * @return The hash-code for the label
     */
    public int hashCode();
    
    /**
     * Determines if this label is equal to a given object.
     *
     * @param other The object to compare to
     * @return True if the objects are equal and false otherwise
     */
    public boolean equals(
        Object other);
}
