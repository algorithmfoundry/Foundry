/*
 * File:                AbstractIterativeAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 30, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.LinkedList;

/**
 * The {@code AbstractIterativeAlgorithm} class implements a simple part of
 * the {@code IterativeAlgorithm} interface that manages the listeners for the 
 * algorithm. It contains methods for firing off the various learning events.
 *
 * @author  Justin Basilico
 * @since   2.0
 * @see     IterativeAlgorithm
 */
@CodeReviews(reviews =
{
    @CodeReview(reviewer = "Kevin R. Dixon",
        date = "2008-02-08",
        changesNeeded = false,
        comments = "Class looks fine."),
    @CodeReview(reviewer = "Justin Basilico",
        date = "2006-10-02",
        changesNeeded = false,
        comments = "Interface is fine.")
})
public abstract class AbstractIterativeAlgorithm
    extends AbstractCloneableSerializable
    implements IterativeAlgorithm
{
    // NOTE: The listeners are accessed directly in the callback firing methods
    // in order to reduce the overhead of firing the callback.
    /** The default iteration number is zero. */
    public static final int DEFAULT_ITERATION = 0;

    /** The objects listening to this algorithm. */
    private transient LinkedList<IterativeAlgorithmListener> listeners;

    /** Number of iterations the algorithm has executed. */
    protected int iteration;

    /**
     * Creates a new instance of {@code AbstractIterativeAlgorithm}.
     */
    public AbstractIterativeAlgorithm()
    {
        this(DEFAULT_ITERATION);
    }

    /**
     * Creates a new instance of {@code AbstractIterativeAlgorithm}.
     *
     * @param   iteration The initial iteration number.
     */
    public AbstractIterativeAlgorithm(
        final int iteration)
    {
        super();

        this.setIteration(iteration);
        this.setListeners(null);
    }

    @Override
    public AbstractIterativeAlgorithm clone()
    {
        final AbstractIterativeAlgorithm result = (AbstractIterativeAlgorithm)
            super.clone();
        result.iteration = 0;
        result.listeners = null;
        return result;
    }

    /**
     * Fires off a algorithm started event for this algorithm, which notifies
     * all of the listeners that the algorithm has started.
     */
    protected void fireAlgorithmStarted()
    {
        if (this.listeners != null)
        {
            for (IterativeAlgorithmListener listener : this.listeners)
            {
                listener.algorithmStarted(this);
            }
        }
        // else - No listeners.
    }

    /**
     * Fires off a algorithm ended event for the algorithm, which notifies all
     * the listeners that the algorithm has ended.
     */
    protected void fireAlgorithmEnded()
    {
        if (this.listeners != null)
        {
            for (IterativeAlgorithmListener listener : this.listeners)
            {
                listener.algorithmEnded(this);
            }
        }
        // else - No listeners.
    }

    /**
     * Fires off a algorithm started event for the algorithm, which notifies
     * all the listeners that a step has started.
     */
    protected void fireStepStarted()
    {
        if (this.listeners != null)
        {
            for (IterativeAlgorithmListener listener : this.listeners)
            {
                listener.stepStarted(this);
            }
        }
        // else - No listeners.
    }

    /**
     * Fires off a algorithm ended event for the algorithm, which notifies all
     * the listeners that a step has ended.
     */
    protected void fireStepEnded()
    {
        if (this.listeners != null)
        {
            for (IterativeAlgorithmListener listener : this.listeners)
            {
                listener.stepEnded(this);
            }
        }
        // else - No listeners.
    }

    public void addIterativeAlgorithmListener(
        final IterativeAlgorithmListener listener)
    {
        if (this.getListeners() == null)
        {
            // No list exists yet so create one.
            this.setListeners(new LinkedList<IterativeAlgorithmListener>());
        }

        // Add the listener.
        this.getListeners().add(listener);
    }

    public void removeIterativeAlgorithmListener(
        final IterativeAlgorithmListener listener)
    {
        if (this.getListeners() == null)
        {
            // No list to remove from.
            return;
        }

        // Remove the listener from the list.
        this.getListeners().remove(listener);

        if (this.getListeners().isEmpty())
        {
            // No more listeners so null out the pointer to make the event
            // firing run faster.
            this.setListeners(null);
        }
    }

    /**
     * Retrieves the list of listeners for this algorithm.
     *
     * @return  The list of current listeners.
     */
    protected LinkedList<IterativeAlgorithmListener> getListeners()
    {
        return this.listeners;
    }

    /**
     * Sets the list of listeners for this algorithm.
     *
     * @param   listeners The new list of listeners.
     */
    protected void setListeners(
        final LinkedList<IterativeAlgorithmListener> listeners)
    {
        this.listeners = listeners;
    }

    public int getIteration()
    {
        return this.iteration;
    }

    /**
     * Sets the current iteration number.
     *
     * @param   iteration Number of iterations executed in the algorithm.
     */
    protected void setIteration(
        final int iteration)
    {
        this.iteration = iteration;
    }

}
