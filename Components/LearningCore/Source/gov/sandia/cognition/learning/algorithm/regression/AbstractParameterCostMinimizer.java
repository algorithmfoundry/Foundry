/*
 * File:                AbstractParameterCostMinimizer.java
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

import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchCostMinimizationLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.cost.SupervisedCostFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * Partial implementation of ParameterCostMinimizer.
 * @param <ResultType> Type of result to expect, such as GradientDescendable
 * @param <CostFunctionType> Type of cost function to use, such as
 * SumSquaredErrorCostFunction
 * @author Kevin R. Dixon
 * @since 2.1
 */
public abstract class AbstractParameterCostMinimizer
        <ResultType extends VectorizableVectorFunction,CostFunctionType extends SupervisedCostFunction<Vector,Vector>>
    extends AbstractAnytimeSupervisedBatchLearner<Vector, Vector, ResultType>
    implements BatchCostMinimizationLearner<Collection<? extends InputOutputPair<? extends Vector, Vector>>,ResultType>,
    ParameterCostMinimizer<ResultType>
{
    
    /**
     * Default convergence criterion {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-7;
        
    /**
     * Default maximum number of iterations before stopping {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;    
    
    /**
     * GradientDescendable whose parameters result minimize the cost function
     */
    private ResultType objectToOptimize;
    
    /**
     * Result to return
     */
    private ResultType result;
    
    /**
     * Stopping criterion for the algorithm, typically ~1e-5
     */
    private double tolerance;    
    
    /**
     * Cost function that computes the cost of the object to optimize
     */
    private CostFunctionType costFunction;
    
    /**
     * Cost of the result
     */
    private Double resultCost;
    
    /**
     * Creates a new instance of AbstractParameterCostMinimizer 
     * @param costFunction
     * Cost function that computes the cost of the object to optimize
     * @param maxIterations
     * Maximum number of iterations before stopping
     * @param tolerance
     * Stopping criterion for the algorithm, typically ~1e-5
     */
    public AbstractParameterCostMinimizer(
        CostFunctionType costFunction,
        int maxIterations,
        double tolerance )
    {
        super( maxIterations );
        this.setCostFunction( costFunction );
        this.setTolerance( tolerance );
        this.setResultCost( null );
    }

    /**
     * Getter for objectToOptimize
     * @return
     * Vectorizable whose parameters result minimize the cost function
     */
    public ResultType getObjectToOptimize()
    {
        return this.objectToOptimize;
    }

    /**
     * Setter for objectToOptimize
     * @param objectToOptimize
     * Vectorizable whose parameters result minimize the cost function
     */
    public void setObjectToOptimize(
        ResultType objectToOptimize )
    {
        this.objectToOptimize = objectToOptimize;
    }

    public ResultType getResult()
    {
        return this.result;
    }
    
    /**
     * Setter for result
     * @param result
     * Result to return
     */
    protected void setResult(
        ResultType result )
    {
        this.result = result;
    }
    
    /**
     * Getter for tolerance
     * @return
     * Stopping criterion for the algorithm, typically ~1e-5
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Stopping criterion for the algorithm, typically ~1e-5
     */
    public void setTolerance(
        double tolerance )
    {
        this.tolerance = tolerance;
    }

    public CostFunctionType getCostFunction()
    {
        return this.costFunction;
    }

    /**
     * Setter for costFunction
     * @param costFunction
     * Cost function that computes the cost of the object to optimize
     */
    public void setCostFunction(
        CostFunctionType costFunction )
    {
        this.costFunction = costFunction;
    }

    /**
     * Getter for resultCost
     * @return
     * Cost of the result
     */
    protected Double getResultCost()
    {
        return this.resultCost;
    }

    /**
     * Setter for resultCost
     * @param resultCost
     * Cost of the result
     */
    protected void setResultCost(
        Double resultCost )
    {
        this.resultCost = resultCost;
    }

    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>(
            ObjectUtil.getShortClassName( this.getCostFunction() ), this.getResultCost() );
    }
    
}
