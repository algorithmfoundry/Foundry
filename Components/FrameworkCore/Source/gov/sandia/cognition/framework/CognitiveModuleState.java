/*
 * File:                CognitiveModuleState.java
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
 * The CognitiveModuleState defines the interface for the state of a
 * CognitiveModule. The reason it exists is because the dynamic state of a
 * module needs to be kept independent and is tied to a specific 
 * CognitiveModelState. The previous CognitiveModuleState is the parameter
 * given to the update function of a CognitiveModule.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 * @see    CognitiveModule
 */
public interface CognitiveModuleState
    extends CloneableSerializable
{
    /**
     * Performs a deep copy of the state.
     *
     * @return A new copy of the state.
     */
    public CognitiveModuleState clone();
}
