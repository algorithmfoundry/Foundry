/*
 * File:                CogxelFactory.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 15, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The CogxelFactory interface defines the functionality required for an object
 * to be used to create a Cogxel for a CognitiveModel.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface CogxelFactory
{
    /**
     * Creates a new Cogxel for the given CogxelFactory from the given
     * SemanticIdentifier. If this factory can create a Cogxel from the given
     * SemanticLabel, then a new Cogxel with that label is created. If the
     * factory cannot create a Cogxel for the given SemanticIdentifier, then
     * null is returned.
     *
     * @param  identifier The SemanticIdentifier of the new Cogxel
     * @return A new Cogxel for the given model from the given identifier, if
     *         the factory can create a Cogxel from the given identifier. If
     *         it cannot, null is returned.
     */
    public Cogxel createCogxel(
        SemanticIdentifier identifier);
}
