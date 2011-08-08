/*
 * File:                CognitiveModelLiteState.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 15, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * The CognitiveModelLiteState class implements a CognitiveModelState
 * object for the CognitiveModelLite.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CognitiveModelLiteState
    extends AbstractCloneableSerializable
    implements CognitiveModelState
{
    /** A flag indicating if the state has been initialized or not. */
    private boolean initialized = false;
    
    /** The input to the model. */
    private CognitiveModelInput input = null;
    
    /** The state of the Cogxels. */
    private CogxelStateLite cogxels = null;
    
    /** The states of the modules. */
    private CognitiveModuleState[] moduleStatesArray = null;
    

    /**
     * Creates a new instance of CognitiveModelState.
     *
     * @param numModules The number of modules in the model.
     */
    public CognitiveModelLiteState(
        int numModules)
    {
        super();
        
        this.setInitialized(false);
        this.setInput(null);
        this.setCogxels(new CogxelStateLite());
        this.setModuleStatesArray(new CognitiveModuleState[numModules]);
    }
    
    /** 
     * Creates a new instance of CognitiveModelState.
     *
     * @param numModules The number of modules in the model.
     * @param expectedMaxIdentifier The expected maximum identifier.
     */
    public CognitiveModelLiteState(
        int numModules,
        int expectedMaxIdentifier)
    {
        super();
        
        this.setInitialized(false);
        this.setInput(null);
        this.setCogxels(new CogxelStateLite(expectedMaxIdentifier));
        this.setModuleStatesArray(new CognitiveModuleState[numModules]);
    }
    
    /**
     * Creates a new copy of a CognitiveModelLiteState.
     *
     * @param other The CognitiveModelLiteState to copy.
     */
    public CognitiveModelLiteState(
        CognitiveModelLiteState other)
    {
        super();
        
        this.setInitialized(other.initialized);
        this.setInput(other.input);
        this.setCogxels(other.cogxels.clone());
        
        int numModules = other.moduleStatesArray.length;
        CognitiveModuleState[] moduleStates = 
            new CognitiveModuleState[numModules];
        
        for (int i = 0; i < numModules; i++)
        {
            if ( other.moduleStatesArray[i] != null )
            {
                moduleStates[i] = other.moduleStatesArray[i].clone();
            }
        }
        
        this.setModuleStatesArray(moduleStates);
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public CognitiveModelLiteState clone()
    {
        final CognitiveModelLiteState clone = (CognitiveModelLiteState)
            super.clone();

        int numModules = this.moduleStatesArray.length;
        clone.cogxels = this.cogxels.clone();
        clone.moduleStatesArray = new CognitiveModuleState[numModules];
        for (int i = 0; i < numModules; i++)
        {
            if (this.moduleStatesArray[i] != null)
            {
                clone.moduleStatesArray[i] = this.moduleStatesArray[i].clone();
            }
        }
        return clone;
    }
    
    /**
     * Clears this CognitiveModelLite state, resetting it to being 
     * uninitialized.
     */
    public void clear()
    {
        // Clear out all of the state data.
        this.setInitialized(false);
        this.setInput(null);
        this.cogxels.clear();
        Arrays.fill(this.moduleStatesArray, null);
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public CognitiveModelInput getInput()
    {
        return this.input;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public CogxelStateLite getCogxels()
    {
        return this.cogxels;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<CognitiveModuleState> getModuleStates()
    {
        return Arrays.asList(this.getModuleStatesArray());
    }
    
    /**
     * Returns the number of module states in this model state. This includes
     * the null states. It should be equal to the number of states in the
     * model from which it was created.
     *
     * @return The number of module states in this model state.
     */
    public int getNumModuleStates()
    {
        return this.moduleStatesArray.length;
    }

    /**
     * Returns true if the state has been initialized.
     *
     * @return True if the state has been initialized.
     */
    public boolean isInitialized()
    {
        return this.initialized;
    }

    /**
     * Sets whether or not this state has been initialized.
     *
     * @param  initialized True if the state has been initialized.
     */
    public void setInitialized(
        boolean initialized)
    {
        this.initialized = initialized;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param input {@inheritDoc}
     */
    public void setInput(
        CognitiveModelInput input)
    {
        this.input = input;
    }

    /**
     * Setter for cogxels
     *
     * @param cogxels The state of the Cogxels.
     */
    protected void setCogxels(
        CogxelStateLite cogxels)
    {
        if ( cogxels == null )
        {
            // Error: The cogxels cannot be null.
            throw new NullPointerException("The cogxels cannot be null.");
        }
        
        this.cogxels = cogxels;
    }

    /**
     * Gets the module states array.
     *
     * @return The array containing module states.
     */
    public CognitiveModuleState[] getModuleStatesArray()
    {
        return this.moduleStatesArray;
    }
    
    /**
     * Sets the array of module states.
     *
     * @param moduleStatesArray The new array of module states.
     */
    protected void setModuleStatesArray(
        CognitiveModuleState[] moduleStatesArray)
    {
        if ( moduleStatesArray == null )
        {
            // Error: The module states cannot be null.
            throw new NullPointerException(
                "The moduleStates cannot be null.");
        }
        
        this.moduleStatesArray = moduleStatesArray;
    }
}

