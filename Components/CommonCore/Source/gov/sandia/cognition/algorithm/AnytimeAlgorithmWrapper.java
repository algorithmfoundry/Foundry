/*
 * File:                AnytimeAlgorithmWrapper.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.util.ObjectUtil;

/**
 * Wraps an AnytimeAlgorithm.  This allows the creation of an AnytimeAlgorithm
 * while using another AnytimeAlgorithm as a wrapped "workhorse" algorithm.
 *
 * @param <ResultType> Result type of the algorithm.
 * @param <InternalAlgorithm> Internal algorithm to wrap.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AnytimeAlgorithmWrapper<ResultType, InternalAlgorithm extends AnytimeAlgorithm>
    extends AbstractIterativeAlgorithm
    implements AnytimeAlgorithm<ResultType>, IterativeAlgorithmListener
{

    /**
     * Underlying algorithm to wrap.
     */
    private InternalAlgorithm algorithm;

    /**
     * Creates a new instance of AnytimeAlgorithmWrapper
     */
    public AnytimeAlgorithmWrapper()
    {
        this(null);
    }

    /** 
     * Creates a new instance of AnytimeAlgorithmWrapper
     * @param algorithm
     * Underlying algorithm to wrap.
     */
    public AnytimeAlgorithmWrapper(
        final InternalAlgorithm algorithm)
    {
        super();
        
        this.setAlgorithm(algorithm);
    }

    @Override
    public AnytimeAlgorithmWrapper<ResultType, InternalAlgorithm> clone()
    {
        @SuppressWarnings("unchecked")
        final AnytimeAlgorithmWrapper<ResultType, InternalAlgorithm> clone =
            (AnytimeAlgorithmWrapper<ResultType, InternalAlgorithm>) super.clone();

        clone.setAlgorithm(ObjectUtil.cloneSmart(this.getAlgorithm()));
        return clone;
    }

    public int getMaxIterations()
    {
        return this.algorithm.getMaxIterations();
    }

    public void setMaxIterations(
        final int maxIterations)
    {
        this.algorithm.setMaxIterations(maxIterations);
    }

    /**
     * Gets the underlying wrapped algorithm.
     *
     * @return
     *      The underlying algorithm being wrapped.
     */
    public InternalAlgorithm getAlgorithm()
    {
        return this.algorithm;
    }

    /**
     * Sets the underlying algorithm.
     *
     * @param algorithm
     *      The underlying algorithm to wrap.
     */
    public void setAlgorithm(
        final InternalAlgorithm algorithm)
    {
        // Remove any previous listener.
        if (this.algorithm != null)
        {
            this.algorithm.removeIterativeAlgorithmListener(this);
        }

        this.algorithm = algorithm;

        // Add a new listener.
        if (this.algorithm != null)
        {
            this.algorithm.addIterativeAlgorithmListener(this);
        }

    }

    public void stop()
    {
        if (this.getAlgorithm() != null)
        {
            this.getAlgorithm().stop();
        }
    }

    @Override
    public int getIteration()
    {
        return this.getAlgorithm().getIteration();
    }

    public boolean isResultValid()
    {
        return (this.getResult() != null);
    }

    public void algorithmStarted(
        final IterativeAlgorithm algorithm)
    {
        this.fireAlgorithmStarted();
    }

    public void algorithmEnded(
        final IterativeAlgorithm algorithm)
    {
        this.fireAlgorithmEnded();
    }

    public void stepStarted(
        final IterativeAlgorithm algorithm)
    {
        this.fireStepStarted();
    }

    public void stepEnded(
        final IterativeAlgorithm algorithm)
    {
        this.fireStepEnded();
    }

    /**
     * This method is detected by the Java Serialization code and is called on
     * deserialization. It allows the object to deal with transient values.
     *
     * @return  Returns this.
     */
    protected Object readResolve()
    {
        if (this.algorithm != null)
        {
            // We need to hook up the listener after deserialization.

            // First remove (just in case the algorithm does serialize 
            // listeners).
            this.algorithm.removeIterativeAlgorithmListener(this);

            // Now add again (to make sure that it is properly hooked up as a
            // listener).
            this.algorithm.addIterativeAlgorithmListener(this);
        }

        // We still resolve as the same object.
        return this;
    }
   
}
