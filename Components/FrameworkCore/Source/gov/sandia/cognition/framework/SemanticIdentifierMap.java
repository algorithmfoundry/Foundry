/*
 * File:                SemanticIdentifierMap.java
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * The SemanticIdentifierMap defines the functionality of a class that
 * assigns identifiers to SemanticLabels and keeps track of them.
 * 
 * 
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since 1.0
 */
public interface SemanticIdentifierMap
    extends Serializable
{
    /**
     * Queries into the map to find a SemanticLabel
     *
     * @param label SemanticLabel to query into the map.
     * @return SemanticIdentifier associated with SemanticLabel, or null if 
     *         not found.
     */
    public SemanticIdentifier findIdentifier(
        SemanticLabel label);

    /**
     * Adds a SemanticLabel to the map, or returns an existing
     * SemanticIdentifier if already in the map
     *
     * @param     label SemanticLabel to add to or retrieve from the map.
     * @return    SemanticIdentifer or null if unable to add.
     * @throws IllegalArgumentException if label is null.
     */
    public SemanticIdentifier addLabel(
        SemanticLabel label);
    
    /**
     * Adds a list of SemanticLabels to the map, returning the list of
     * the corresponding SemanticIdentifiers for the given SemanticLabels.
     *
     * @param  labels The SemanticLabels to add.
     * @return The list of SemanticIdentifiers corresponding to the given
     *         SemanticLabels.
     * @throws IllegalArgumentException If one of the labels is null.
     */
    public ArrayList<SemanticIdentifier> addLabels(
        Collection<SemanticLabel> labels);
    
    /**
     * Gets all the SemanticIdentifiers in the map.
     *
     * @return All the SemanticIdentifiers in the map.
     */
    public Collection<SemanticIdentifier> getIdentifiers();
    
    /**
     * Adds a listener to this semantic identifier map.
     *
     * @param  listener The listener to add.
     */
    public void addSemanticIdentifierMapListener(
        SemanticIdentifierMapListener listener);
    
    /**
     * Removes a listener from this semantic identifier map.
     *
     * @param  listener The listener to remove.
     */
    public void removeSemanticIdentifierMapListener(
        SemanticIdentifierMapListener listener);
}
