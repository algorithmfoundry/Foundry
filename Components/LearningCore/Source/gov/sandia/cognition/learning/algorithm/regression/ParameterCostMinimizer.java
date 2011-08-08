/*
 * File:                ParameterCostMinimizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 4, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.algorithm.AnytimeAlgorithm;
import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.learning.algorithm.BatchCostMinimizationLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import java.util.Collection;

/**
 * A anytime algorithm that is used to estimate the locally minimum-cost
 * parameters of an object.
 * 
 * @param <ResultType>
 * Type of parameterizable object to determine the locally minimum-cost
 * parameters for.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public interface ParameterCostMinimizer<ResultType extends VectorizableVectorFunction>
    extends SupervisedBatchLearner<Vector, Vector, ResultType>,
    BatchCostMinimizationLearner<Collection<? extends InputOutputPair<? extends Vector, Vector>>,ResultType>,
    AnytimeAlgorithm<ResultType>,
    MeasurablePerformanceAlgorithm
{
    
    /**
     * Gets the object to optimize
     * @return
     * object to optimize
     */
    public ResultType getObjectToOptimize();
    
    /**
     * Set the object to optimize
     * @param objectToOptimize
     * object to optimize
     */
    public void setObjectToOptimize(
        ResultType objectToOptimize );
    
}
