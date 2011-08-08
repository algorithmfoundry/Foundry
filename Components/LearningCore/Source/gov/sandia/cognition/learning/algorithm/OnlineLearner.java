/*
 * File:                OnlineLearner.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The {@code OnlineLearner} interface defines the general functionality
 * of an object that is the implementation of a data-driven, online machine 
 * learning algorithm. It is the analog of the {@code BatchLearner}
 * interface but for online learning.
 * <BR><BR>
 * The design pattern for machine learning algorithms is to have all the
 * parameters and configuration of the algorithm set on the {@code BatchLearner}
 * object (usually as a Java Bean) and then the data is passed in using the
 * learn method to create the result object of the learning algorithm.
 * <BR><BR>
 * The interface is for an "online" machine learning algorithm because the 
 * learn method is expected to update a given object of the {@code ResultType} 
 * using the new given data.
 * <BR><BR>
 * Generally, algorithms that implement the OnlineLearner interface should also
 * implement the BatchLearner interface because an OnlineLearner can always be
 * used in a batch situation. However, this is not currently enforced by the
 * interface definition because an algorithm may expect different data when
 * it is operating in "batch" versus "online" modes.
 * <BR><BR>
 * If you implement this without really using a learning algorithm you will 
 * make the authors very sad (especially Justin).
 *
 * @param   <DataType> Data used to train the {@code ResultType}.
 * @param   <ResultType> The type of object created and modified by the 
 *          learning algorithm. For example, a {@code FeedforwardNeuralNetwork}.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.0
 * @see     BatchLearner
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Fixed a couple of javadoc typos.",
        "Interface looks fine."
    }
)
public interface OnlineLearner<DataType, ResultType>
    extends CloneableSerializable
{
    /**
     * Creates a new initial learned object, before any data is given.
     *
     * @return  The initial learned object.
     */
    public ResultType createInitialLearnedObject();
    
    /**
     * The {@code update} method updates an object of {@code ResultType} using
     * the given new data of type {@code DataType}, using some form of 
     * "learning" algorithm.
     *
     * @param   target
     *      The object to update.
     * @param   data
     *      The new data for the learning algorithm to use to update
     *      the object.
     */
    public void update(
        ResultType target,
        DataType data);

    /**
     * The {@code update} method updates an object of {@code ResultType} using
     * the given new Iterable containing some number of type {@code DataType},
     * using some form of "learning" algorithm.
     * @param target
     * The object to update
     * @param data
     * The new Iterable containing data for the learning algorithm to use to
     * update the object.
     */
    public void update(
        ResultType target,
        Iterable<? extends DataType> data );

}
