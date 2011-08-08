/*
 * File:                CogxelState.java
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
import java.util.Collection;

/**
 * Keeps a collection of Cogxels and some accessor methods.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface CogxelState
    extends java.lang.Iterable<Cogxel>,
    CloneableSerializable
{   
    /**
     * Adds a Cogxel to the state, overriding the existing Cogxel, if it
     * exists.
     *
     * @param  cogxel The Cogxel to add.
     * @throws IllegalArgumentException If an invalid Cogxel is given.
     */
    public void addCogxel(
        Cogxel cogxel);
    
    /**
     * Returns true if there is an existing Cogxel for the given identifier and
     * false otherwise.
     *
     * @param  identifier The identifier to look for the Cogxel.
     * @return True if there is a Cogxel for the given identifier.
     */
    public boolean hasCogxel(
        SemanticIdentifier identifier);
    
    /**
     * Gets a Cogxel from the state, if it has been previously added.
     *
     * @param  identifier The SemanticIdentifier for the Cogxel.
     * @return If the SemanticIdentifier exists in the CogxelState, then return
     * the corresponding Cogxel.  Otherwise, return null.
     * @throws IllegalArgumentException If an invalid identifier is given.
     */
    public Cogxel getCogxel(
        SemanticIdentifier identifier);
    
    /**
     * Attempts to get an existing Cogxel for a given SemanticIdentifier. If
     * no such Cogxel exists, it uses the given CogxelFactory to create the
     * Cogxel and adds it to the CogxelState. In either case, as long as a valid
     * SemanticIdentifier is given, it will return a Cogxel.
     *
     * @param  identifier The identifier to get or create a Cogxel for.
     * @param  factory The factory to use to create the Cogxel is one does not
     *         already exist for the identifier.
     * @return A Cogxel for the given identifier.
     * @throws IllegalArgumentException If an invalid identifier is given.
     * @throws NullPointerException If an invalid factory is given.
     */
    public Cogxel getOrCreateCogxel(
        SemanticIdentifier identifier,
        CogxelFactory factory);
    
    /**
     * Removes a Cogxel from the state, if it exists.
     *
     * @param  identifier The SemanticIdentifier of the Cogxel to remove.
     * @return True if the Cogxel was sucessfully removed, false otherwise.
     * @throws IllegalArgumentException If an invalid identifier is given.
     */
    public boolean removeCogxel(
        SemanticIdentifier identifier);
    
    /**
     * Removes a Cogxel from the CogxelState, if it exists.
     *
     * @param  cogxel The Cogxel to remove.
     * @return True if the Cogxel was sucessfully removed, false otherwise.
     * @throws IllegalArgumentException If an invalid Cogxel is given.
     */ 
    public boolean removeCogxel(
        Cogxel cogxel);
    
    /**
     * Gets the activation level of a Cogxel in the CogxelState. If the
     * associated Cogxel is null then 0.0 is returned, otherwise the
     * getActivation method is called on the underlying Cogxel.
     *
     * @param  identifier The SemanticIdentifier for the Cogxel to get the
     * activation level of.
     * @return The activation level of the underlying Cogxel, or 0.0 if the
     * Cogxel doesn't exist in the CogxelState.
     * @throws IllegalArgumentException If an invalid identifier is given.
     */
    public double getCogxelActivation(
        SemanticIdentifier identifier);
    
    /**
     * Gets the Cogxels that have been previously added to this CogxelState.
     *
     * @return The previously added Cogxels.
     */
    public Collection<Cogxel> getCogxels();
    
    /**
     * Gets the number of existing Cogxels in this CogxelState.
     *
     * @return The number of existing Cogxels in this CogxelState.
     */
    public int getNumCogxels();
    
    /**
     * Clones this Cogxel state, returning a deep copy of the Cogxels.
     *
     * @return A copy of this CogxelState.
     */
    public CogxelState clone();
}
