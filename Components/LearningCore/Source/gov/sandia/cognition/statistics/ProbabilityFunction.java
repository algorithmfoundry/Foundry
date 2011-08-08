/*
 * File:                ProbabilityFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.evaluator.Evaluator;

/**
 * A Distribution that has an evaluate method that indicates p(x), such as
 * a probability density function or a probability mass function (but NOT
 * a cumulative distribution function).
 * 
 * @param <DataType> 
 * Type of data that can be sampled from the distribution, and also the
 * input to the evaluate method.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ProbabilityFunction<DataType>
    extends ComputableDistribution<DataType>,
    Evaluator<DataType,Double>
{

    /**
     * Evaluate the natural logarithm of the distribution function.
     * This is sometimes more efficient than evaluating the distribution
     * function itself, and when evaluating the product of many independent
     * or exchangeable samples.
     * @param input
     * @return
     * Natural logarithm of the distribution function.
     */
    public double logEvaluate(
        DataType input );

}
