/*
 * File:            AbstractIterativeAlgorithmListener.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.algorithm.event;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.IterativeAlgorithmListener;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@link IterativeAlgorithmListener}
 * interface that provides default implementations of the event methods that
 * do nothing. It is meant so that listeners only need to implement the methods
 * for the events they want to listen to.
 *
 * @author  Justin Basilico
 * @version 3.4.0
 * @see     IterativeAlgorithmListener
 */
public abstract class AbstractIterativeAlgorithmListener
    extends AbstractCloneableSerializable
    implements IterativeAlgorithmListener
{

    /**
     * Creates a new {@code AbstractIterativeAlgorithmListener}.
     */
    public AbstractIterativeAlgorithmListener()
    {
        super();
    }

    @Override
    public void algorithmStarted(
        final IterativeAlgorithm algorithm)
    {
        // Do nothing.
    }

    @Override
    public void algorithmEnded(
        final IterativeAlgorithm algorithm)
    {
        // Do nothing.
    }

    @Override
    public void stepStarted(
        final IterativeAlgorithm algorithm)
    {
        // Do nothing.
    }

    @Override
    public void stepEnded(
        final IterativeAlgorithm algorithm)
    {
        // Do nothing.
    }
    
}
