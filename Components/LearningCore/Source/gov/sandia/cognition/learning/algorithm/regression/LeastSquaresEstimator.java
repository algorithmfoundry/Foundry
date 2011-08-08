/*
 * File:                LeastSquaresEstimator.java
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

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;

/**
 * Abstract implementation of iterative least-squares estimators.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public abstract class LeastSquaresEstimator 
    extends AbstractParameterCostMinimizer<GradientDescendable,SumSquaredErrorCostFunction>
{
    
    /** 
     * Creates a new instance of LeastSquaresEstimator 
     * @param maxIterations 
     * Maximum number of iterations before stopping
     * @param tolerance 
     * Stopping criterion for the algorithm, typically ~1e-5
     */
    public LeastSquaresEstimator(
        int maxIterations,
        double tolerance )
    {
        super( new SumSquaredErrorCostFunction(), maxIterations, tolerance );
    }

}
