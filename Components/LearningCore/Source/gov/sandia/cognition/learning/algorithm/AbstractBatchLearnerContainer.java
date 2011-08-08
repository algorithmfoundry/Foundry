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
 * @param <LearnerType>
 *      Type of the learning algorithm contained in the object.
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractBatchLearnerContainer<LearnerType extends BatchLearner<?, ?>>
    extends AbstractCloneableSerializable
    implements BatchLearnerContainer<LearnerType>
{

    /** The wrapped learner. */
    protected LearnerType learner;

    /**
     * Creates a new {@code AbstractBatchLearnerWrapper} with a null learner.
     */
    public AbstractBatchLearnerContainer()
    {
        this(null);
    }

    /**
     * Creates a new {@code AbstractBatchLearnerWrapper} with the given learner.
     *
     * @param   learner
     *      The wrapped learner.
     */
    public AbstractBatchLearnerContainer(
        final LearnerType learner)
    {
        super();

        this.setLearner(learner);
    }

    @Override
    public AbstractBatchLearnerContainer<LearnerType> clone()
    {
        @SuppressWarnings("unchecked")
        final AbstractBatchLearnerContainer<LearnerType> clone =
            (AbstractBatchLearnerContainer<LearnerType>) super.clone();
        clone.setLearner(ObjectUtil.cloneSafe(this.getLearner()));
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
