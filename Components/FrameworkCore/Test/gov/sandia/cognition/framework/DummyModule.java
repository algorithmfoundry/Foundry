/*
 * File:                DummyModule.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The DummyModule class implements a dummy module used for testing.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DummyModule
    extends java.lang.Object
    implements CognitiveModule
{
    /**
     * Creates a new instance of DummyModule.
     */
    public DummyModule()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param  modelState {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModuleState initializeState(
        CognitiveModelState modelState)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  modelState {@inheritDoc}
     * @param  previousModuleState {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModuleState update(
        CognitiveModelState modelState, 
        final CognitiveModuleState previousModuleState)
    {
        return null;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getName()
    {
        return "Dummy Module";
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public CognitiveModuleSettings getSettings()
    {
        return null;
    }
}

