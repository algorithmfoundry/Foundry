/*
 * File:                BatchAndIncrementalLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 06, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm;

import java.util.Collection;

/**
 * Interface for an algorithm that is both a batch and incremental learner.
 *
 * @param   <DataType>
 *      Type of the input data.
 * @param   <ResultType>
 *      Result type from both the online- and batch-learning interfaces.
 * @author  Justin Basilico
 * @since   3.1.2
 */
public interface BatchAndIncrementalLearner<DataType, ResultType>
    extends BatchLearner<Collection<? extends DataType>, ResultType>,
        IncrementalLearner<DataType, ResultType>
{
    /**
     * Creates an object of {@code ResultType} using
     * data of type {@code DataType}, using some form of "learning" algorithm.
     * Typically implemented as a convenience method for calling an incremental
     * learner on each data point.
     *
     * @param   data
     *      The data that the learning algorithm will use to create an object
     *      of {@code ResultType}.
     * @return
     *      The object that is created based on the given data using the
     *      learning algorithm.
     */
    public ResultType learn(
        final Iterable<? extends DataType> data);
    
}
