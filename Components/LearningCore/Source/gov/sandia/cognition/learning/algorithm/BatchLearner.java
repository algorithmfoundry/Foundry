/*
 * File:                BatchLearner.java
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
 * The {@code BatchLearner} interface defines the general functionality
 * of an object that is the implementation of a data-driven, batch  machine 
 * learning algorithm. It unifies the interfaces for both supervised and 
 * unsupervised learning, but as such is extremely general. The interface 
 * defines that a learning algorithm takes in some type of data and produces 
 * some type of object as output from the data.
 * <BR><BR>
 * Typically the input to the learning algorithm will be some collection of
 * data (training data) and the output will be some form of pattern recognizer 
 * (classifier/categorizer).
 * <BR><BR>
 * The design pattern for machine learning algorithms is to have all the
 * parameters and configuration of the algorithm set on the {@code BatchLearner}
 * object (usually as a Java Bean) and then the data is passed in using the
 * learn method to create the result object of the learning algorithm.
 * <BR><BR>
 * The interface is for a "batch" machine learning algorithm because the call
 * to the learn method is expected to create a usable object from scratch using
 * the provided data.
 * <BR><BR>
 * If you implement this without really using a learning algorithm you will 
 * make the authors very sad (especially Justin).
 *
 * @param   <DataType> The type of the data that the algorithm uses to perform
 *          the learning. For example, a 
 *          {@code Collection<InputOutputPair<Vector, Double>>} or 
 *          {@code String}.
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code FeedforwardNeuralNetwork}.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.0
 * @see     IncrementalLearner
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Interface looks fine."
)
public interface BatchLearner<DataType, ResultType>
    extends CloneableSerializable
{
    /**
     * The {@code learn} method creates an object of {@code ResultType} using 
     * data of type {@code DataType}, using some form of "learning" algorithm.
     *
     * @param   data The data that the learning algorithm will use to create an
     *          object of {@code ResultType}.
     * @return  The object that is created based on the given data using the
     *          learning algorithm.
     */
    ResultType learn(
        DataType data);
}
