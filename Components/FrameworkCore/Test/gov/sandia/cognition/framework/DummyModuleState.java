/*
 * File:                DummyModuleState.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * Dummy CognitiveModuleState for testing purposes.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DummyModuleState
    extends java.lang.Object
    implements CognitiveModuleState
{
    /**
     * Creates a new instance of DummyModuleState
     */
    public DummyModuleState()
    {
        super();
    }
    
    /**
     * Creates a new instance of DummyModuleState
     * @param other DummyModuleState to copy
     */
    public DummyModuleState(
        DummyModuleState other)
    {
        super();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public DummyModuleState clone()
    {
        return new DummyModuleState(this);
    }
}

