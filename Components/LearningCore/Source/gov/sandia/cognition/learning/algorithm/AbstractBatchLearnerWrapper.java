/*
 * File:                BatchLearnerWrapper.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 08, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * An abstract class for objects that contain a batch learning algorithm.
 * 
 * @param <DataType> Type of data
 * @param <LearnedType> Result type from the learning algorithm
 * @param <LearnerType> Learning algorithm to wrap
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractBatchLearnerWrapper<DataType, LearnedType, LearnerType extends BatchLearner<? super DataType, ? extends LearnedType>>
    extends AbstractCloneableSerializable
{

    /** The wrapped learner. */
    protected LearnerType learner;

    /**
     * Creates a new {@code AbstractBatchLearnerWrapper} with a null learner.
     */
    public AbstractBatchLearnerWrapper()
    {
        this(null);
    }

    /**
     * Creates a new {@code AbstractBatchLearnerWrapper} with the given learner.
     *
     * @param   learner
     *      The wrapped learner.
     */
    public AbstractBatchLearnerWrapper(
        final LearnerType learner)
    {
        super();

        this.setLearner(learner);
    }

    @Override
    public AbstractBatchLearnerWrapper<DataType, LearnedType, LearnerType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractBatchLearnerWrapper<DataType, LearnedType, LearnerType> clone =
            (AbstractBatchLearnerWrapper<DataType, LearnedType, LearnerType>) super.clone();
        clone.setLearner( ObjectUtil.cloneSafe(this.getLearner() ) );
        return clone;
    }

    /**
     * Gets the wrapped learner.
     *
     * @return
     *      The wrapped learner.
     */
    public LearnerType getLearner()
    {
        return this.learner;
    }

    /**
     * Sets the wrapped learner.
     *
     * @param   learner
     *      The wrapped learner.
     */
    public void setLearner(
        final LearnerType learner)
    {
        this.learner = learner;
    }

}
