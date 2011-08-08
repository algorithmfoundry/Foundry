/*
 * File:                CognitiveModuleStateWrapper.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The CognitiveModuleStateWrapper wraps some other object as a 
 * CognitiveModuleState object. The only condition is that the internal object
 * must be Cloneable and Serializable.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class CognitiveModuleStateWrapper
    extends AbstractCloneableSerializable
    implements CognitiveModuleState
{
    /** The internal state object that is wrapped. */
    private CloneableSerializable internalState = null;
    
    /** 
     * Creates a new instance of CognitiveModuleStateWrapper. The initial
     * state object is null.
     */
    public CognitiveModuleStateWrapper()
    {
        super();
        
        this.setInternalState(null);
    }
    
    /** 
     * Creates a new instance of CognitiveModuleStateWrapper.
     *
     * @param  internalState The internal state object.
     */
    public CognitiveModuleStateWrapper(
        CloneableSerializable internalState)
    {
        super();
        
        this.setInternalState(internalState);
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public CognitiveModuleStateWrapper clone()
    {
        final CognitiveModuleStateWrapper clone =
            (CognitiveModuleStateWrapper) super.clone();
        clone.internalState = ObjectUtil.cloneSafe(this.internalState);
        return clone;
    }

    /**
     * Gets the internal state object.
     *
     * @return The internal state object.
     */
    public CloneableSerializable getInternalState()
    {
        return internalState;
    }

    /**
     * Sets the internal state object.
     *
     * @param  internalState The internal state object.
     */
    public void setInternalState(
        CloneableSerializable internalState)
    {
        this.internalState = internalState;
    }
}