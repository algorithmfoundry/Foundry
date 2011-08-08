/*
 * File:                BatchLearnerWrapper.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm;

/**
 * An interface for an object that contains a batch learner.
 * 
 * @param   <LearnerType>
 *      The type of the learner contained in the object.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface BatchLearnerContainer<LearnerType extends BatchLearner<?, ?>>
{

    /**
     * Gets the learner contained in this object.
     *
     * @return
     *      The learner contained in this object.
     */
    public LearnerType getLearner();

}
