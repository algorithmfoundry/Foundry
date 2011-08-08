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
import java.util.Collection;

/**
 * The CognitiveModelState interface defines the general functionality required
 * of an object that represents the state of a CognitiveModel.  This should
 * contain the complete persistent storage of the CognitiveModel and should
 * not be operated on by side-effect except by objects contained within the
 * CognitiveModel.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface CognitiveModelState
    extends CloneableSerializable
{
    /**
     * A deep copy clone of this state.
     *
     * @return A deep copy of this state.
     */
    public CognitiveModelState clone();
    
    /**
     * Gets the input to the model for this state.
     *
     * @return The model input.
     */
    public CognitiveModelInput getInput();

    /**
     * Gets the CogxelState.
     *
     * @return The current state of the Cogxels.
     */
    public CogxelState getCogxels();
    
    /**
     * Gets the collection of module states.
     *
     * @return The collection of module states.
     */
    public Collection<CognitiveModuleState> getModuleStates();

    /** 
     * Sets the input for this model state.
     *
     * @param input The new input.
     */
    public void setInput(
        CognitiveModelInput input);
}
