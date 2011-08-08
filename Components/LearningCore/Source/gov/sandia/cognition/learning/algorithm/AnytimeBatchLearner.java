/*
 * File:                AnytimeBatchLearner.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 10, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.algorithm.AnytimeAlgorithm;

/**
 * A batch learner that is also and Anytime algorithm.
 * @param   <ResultType> The type of object created by the learning algorithm.
 *          For example, a {@code FeedforwardNeuralNetwork}.
 * @param   <DataType> The type of the data that the algorithm uses to perform
 *          the learning. For example, a
 *          {@code Collection<InputOutputPair<Vector, Double>>} or
 *          {@code String}.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface AnytimeBatchLearner<DataType,ResultType>
    extends AnytimeAlgorithm<ResultType>,
    BatchLearner<DataType, ResultType>
{

    /**
     * Gets the keep going value, which indicates if the algorithm should
     * continue on to another step.
     *
     * @return  The keep going value.
     */
    public boolean getKeepGoing();

    /**
     * Gets the data to use for learning. This is set when learning starts
     * and then cleared out once learning is finished.
     *
     * @return  The data to use for learning.
     */
    public DataType getData();

}
