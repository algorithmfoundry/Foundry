/*
 * File:                Cogxel.java
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

import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The interface for the fundamental unit of operation inside a CognitiveModel.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface Cogxel
    extends CloneableSerializable
{
    /**
     * Clones the Cogxel.
     *
     * @return A deep copy of the Cogxel.
     */
    public Cogxel clone();
    
    /**
     * Gets the SemanticIdentifier for the Cogxel.
     *
     * @return The SemanticIdentifier for the Cogxel.
     */
    public SemanticIdentifier getSemanticIdentifier();

    /**
     * Gets the SemanticLabel for the Cogxel.
     *
     * @return The SemanticLabel for the Cogxel.
     */
    public SemanticLabel getSemanticLabel();

    /**
     * Gets the current activation level of the Cogxel
     *
     * @return The current activation level.
     */
    public double getActivation();
    
    /**
     * Sets the activation level of the Cogxel.
     *
     * @param activation The new activation level.
     */
    public void setActivation(
        double activation);
}
