/*
 * File:                ParameterDerivativeFreeCostMinimizer.java
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
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizerDirectionSetPowell;
import gov.sandia.cognition.learning.function.cost.SupervisedCostFunction;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implementation of a class of objects that uses a derivative-free
 * minimization algorithm.  That is, this class estimates locally minimum-cost
 * parameter sets without needing first-order derivative information.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class ParameterDerivativeFreeCostMinimizer 
    extends AbstractMinimizerBasedParameterCostMinimizer<VectorizableVectorFunction,DifferentiableEvaluator<Vector,Double,Vector>>
{
    
    /**
     * Default function minimizer, FunctionMinimizerDirectionSetPowell
     */
    public static final FunctionMinimizer<Vector,Double,Evaluator<? super Vector,Double>> DEFAULT_FUNCTION_MINIMIZER =
        new FunctionMinimizerDirectionSetPowell(); 
    
    /** 
     * Creates a new instance of ParameterDerivativeFreeCostMinimizer 
     */
    @SuppressWarnings("unchecked")
    public ParameterDerivativeFreeCostMinimizer()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_FUNCTION_MINIMIZER ) );
    }
    
    /**
     * Creates a new instance of ParameterDerivativeFreeCostMinimizer 
     * @param minimizer
     * Minimization algorithm used to find locally optimal parameters
     */
    public ParameterDerivativeFreeCostMinimizer(
        FunctionMinimizer<Vector,Double,? super DifferentiableEvaluator<Vector,Double,Vector>> minimizer )
    {
        super( minimizer );
    }

    @Override
    public ParameterDerivativeFreeCostMinimizer clone()
    {
        return (ParameterDerivativeFreeCostMinimizer) super.clone();
    }

    @Override
    public ParameterCostEvaluatorDerivativeFree createInternalFunction()
    {
        return new ParameterCostEvaluatorDerivativeFree(
            this.getResult(), this.getCostFunction() );
    }        
    
    /**
     * Function that maps the parameters of an object to its inputs, so that
     * minimization algorithms can tune the parameters of an object against
     * a cost function.  Uses forward differences to estimate derivatives, if
     * necessary.
     */
    public static class ParameterCostEvaluatorDerivativeFree
        extends AbstractCloneableSerializable
        implements DifferentiableEvaluator<Vector,Double,Vector>
    {
        
        /**
         * Object that we're tweaking the parameters of.
         */
        private VectorizableVectorFunction internalFunction;
        
        /**
         * Cost function against which to evaluate the cost of the object.
         */
        private SupervisedCostFunction<Vector,Vector> costFunction;
        
        /**
         * Creates a new instance of ParameterCostEvaluatorDerivativeFree
         * @param internalFunction
         * Object that we're tweaking the parameters of.
         * @param costFunction
         * Cost function against which to evaluate the cost of the object.
         */
        public ParameterCostEvaluatorDerivativeFree(
            VectorizableVectorFunction internalFunction,
            SupervisedCostFunction<Vector,Vector> costFunction )
        {
            this.internalFunction = internalFunction;
            this.costFunction = costFunction;
        }

        public Vector differentiate(
            Vector input )
        {
            return NumericalDifferentiator.VectorJacobian.differentiate( input, this );
        }

        public Double evaluate(
            Vector input )
        {
            this.internalFunction.convertFromVector( input );
            return this.costFunction.evaluate( this.internalFunction );
        }

        @Override
        public ParameterCostEvaluatorDerivativeFree clone()
        {
            ParameterCostEvaluatorDerivativeFree clone =
                (ParameterCostEvaluatorDerivativeFree) super.clone();
            clone.costFunction = ObjectUtil.cloneSafe( this.costFunction );
            clone.internalFunction =
                ObjectUtil.cloneSafe( this.internalFunction );
            return clone;
        }
        
    }

}
