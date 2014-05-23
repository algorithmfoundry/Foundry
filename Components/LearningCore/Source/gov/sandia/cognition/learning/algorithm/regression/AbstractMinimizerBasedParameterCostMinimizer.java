/*
 * File:                AbstractMinimizerBasedParameterCostMinimizer.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.algorithm.AnytimeAlgorithmWrapper;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.cost.DifferentiableCostFunction;
import gov.sandia.cognition.learning.function.cost.ParallelizedCostFunctionContainer;
import gov.sandia.cognition.learning.function.cost.SumSquaredErrorCostFunction;
import gov.sandia.cognition.learning.function.cost.SupervisedCostFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;

/**
 * Partial implementation of ParameterCostMinimizer, based on the algorithms
 * from the minimization package.  These classes apply minimization algorithms
 * to the purpose of estimating locally minimum-cost parameter sets of an
 * object.
 * 
 * @param <ResultType> Type of result to expect, such as GradientDescendable
 * @param <EvaluatorType> Type of Evaluator to use internally, such as
 * DifferentiableEvaluator
 * @author Kevin R. Dixon
 * @since 2.1
 */
public abstract class AbstractMinimizerBasedParameterCostMinimizer
        <ResultType extends VectorizableVectorFunction,
        EvaluatorType extends Evaluator<? super Vector,? extends Double>>
    extends AnytimeAlgorithmWrapper<ResultType,FunctionMinimizer<Vector,Double,? super EvaluatorType>>
    implements ParameterCostMinimizer<ResultType>
{

    /**
     * Object that is being optimized.
     */
    private ResultType objectToOptimize;
    
    /**
     * Resulting value
     */
    private ResultType result;
    
    /**
     * Cost function to compute the cost of objectToOptimize
     */
    private SupervisedCostFunction<Vector,Vector> costFunction;
    
    /**
     * Default cost function, {@code SumSquaredErrorCostFunction}
     */
    public static final SupervisedCostFunction<Vector, Vector> DEFAULT_COST_FUNCTION =
        new ParallelizedCostFunctionContainer( new SumSquaredErrorCostFunction() );
    

    /**
     * Creates a new instance of AbstractMinimizerBasedParameterCostMinimizer
     * @param algorithm
     * Minimization algorithm to use.
     */
    public AbstractMinimizerBasedParameterCostMinimizer(
        FunctionMinimizer<Vector,Double,? super EvaluatorType> algorithm )
    {
        this( algorithm, DEFAULT_COST_FUNCTION );
    }
    
    /** 
     * Creates a new instance of AbstractMinimizerBasedParameterCostMinimizer 
     * @param algorithm
     * Minimization algorithm to use.
     * @param costFunction 
     * Cost function to compute the cost of objectToOptimize
     */
    public AbstractMinimizerBasedParameterCostMinimizer(
        FunctionMinimizer<Vector,Double,? super EvaluatorType> algorithm,
        SupervisedCostFunction<Vector,Vector> costFunction )
    {
        super( algorithm );
        this.setCostFunction( costFunction );
    }

    @Override
    public AbstractMinimizerBasedParameterCostMinimizer<ResultType,EvaluatorType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractMinimizerBasedParameterCostMinimizer<ResultType,EvaluatorType> clone =
            (AbstractMinimizerBasedParameterCostMinimizer<ResultType,EvaluatorType>) super.clone();
        clone.setObjectToOptimize( ObjectUtil.cloneSafe( this.getObjectToOptimize() ) );
        clone.setResult( ObjectUtil.cloneSafe( this.getResult() ) );
        clone.setCostFunction( ObjectUtil.cloneSafe( this.getCostFunction() ) );
        return clone;
    }

    /**
     * Creates the internal function that maps the parameter set of
     * result as the input to the function, so that the minimization
     * algorithms can perturb this input in their minimization schemes.
     * @return
     * Evaluator to use internally.
     */
    abstract public EvaluatorType createInternalFunction();    
    
    public ResultType getObjectToOptimize()
    {
        return this.objectToOptimize;
    }

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
     * Result of the minimization
     */
    public void setResult(
        ResultType result )
    {
        this.result = result;
    }

    @SuppressWarnings("unchecked")
    public ResultType learn(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> data )
    {
        this.getCostFunction().setCostParameters( data );

        this.setResult( (ResultType) this.getObjectToOptimize().clone() );
        
        Vector parameters = this.getResult().convertToVector();
        
        this.getAlgorithm().setInitialGuess( parameters );
        
        EvaluatorType internalFunction = this.createInternalFunction();
        InputOutputPair<Vector,Double> bestParameters = 
            this.getAlgorithm().learn( internalFunction );
        
        this.getResult().convertFromVector( bestParameters.getInput() );
        
        return this.getResult();
    }

    public SupervisedCostFunction<Vector,Vector> getCostFunction()
    {
        return this.costFunction;
    }

    /**
     * Setter for costFunction
     * @param costFunction
     * Cost function that maps the object to optimize onto a scalar cost
     */
    public void setCostFunction( 
        SupervisedCostFunction<Vector,Vector> costFunction )
    {
        this.costFunction = costFunction;
    }
        
    /**
     * Gets the performance, which is the cost of the minimizer on the last 
     * iteration.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Double> getPerformance()
    {
        Double performance = (this.getAlgorithm().getResult() != null)
            ? this.getAlgorithm().getResult().getSecond() : null;
        return new DefaultNamedValue<Double>(
            "cost", performance );
    }
}
