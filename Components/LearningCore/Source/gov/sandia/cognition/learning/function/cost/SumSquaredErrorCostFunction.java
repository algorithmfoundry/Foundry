/*
 * File:                SumSquaredErrorCostFunction.java
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

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultPair;
import java.util.Collection;

/**
 * This is the sum-squared error cost function
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class SumSquaredErrorCostFunction 
    extends AbstractParallelizableCostFunction
{

    /** 
     * Creates a new instance of SumSquaredErrorCostFunction 
     */
    public SumSquaredErrorCostFunction()
    {
        this( (Collection<? extends InputOutputPair<? extends Vector, Vector>>) null );
    }

    /** 
     * Creates a new instance of MeanSquaredErrorCostFunction
     *
     * @param dataset The dataset of examples to use to compute the error.
     */
    public SumSquaredErrorCostFunction(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> dataset )
    {
        super( dataset );
    }

    @Override
    public SumSquaredErrorCostFunction clone()
    {
        return (SumSquaredErrorCostFunction) super.clone();
    }

    public Object evaluatePartial(
        Evaluator<? super Vector, ? extends Vector> evaluator )
    {
        double sumSquaredError = 0.0;
        double weightSum = 0.0;
        for (InputOutputPair<? extends Vector,Vector> pair : this.getCostParameters() )
        {
            // Compute the error vector.
            Vector target = pair.getOutput();
            Vector estimate = evaluator.evaluate( pair.getInput() );
            double errorSquared = target.euclideanDistanceSquared( estimate );
            double weight = DatasetUtil.getWeight(pair);
            weightSum += weight;
            sumSquaredError += weight * errorSquared;
        }
        weightSum *= 2.0;
        
        return new EvaluatePartialSSE( sumSquaredError, weightSum );
    }

    
    public Double evaluateAmalgamate(
        Collection<Object> partialResults )
    {
        double numerator = 0.0;
        double denominator = 0.0;
        
        for( Object result : partialResults )
        {
            EvaluatePartialSSE sse = (EvaluatePartialSSE) result;
            numerator += sse.getFirst();
            denominator += sse.getSecond();
        }
        
        if( denominator == 0.0 )
        {
            return 0.0;
        }
        else
        {
            return numerator / denominator;
        }
    }

    public Object computeParameterGradientPartial(
        GradientDescendable function )
    {
        RingAccumulator<Vector> parameterDelta =
            new RingAccumulator<Vector>();

        double denominator = 0.0;

        for (InputOutputPair<? extends Vector, ? extends Vector> pair : this.getCostParameters())
        {
            Vector input = pair.getInput();
            Vector target = pair.getOutput();

            Vector negativeError = function.evaluate( input );
            negativeError.minusEquals( target );

            double weight = DatasetUtil.getWeight(pair);

            if (weight != 1.0)
            {
                negativeError.scaleEquals( weight );
            }

            denominator += weight;

            Matrix gradient = function.computeParameterGradient( input );
            Vector parameterUpdate = negativeError.times( gradient );
            parameterDelta.accumulate( parameterUpdate );
        }

        Vector negativeSum = parameterDelta.getSum();
        return new GradientPartialSSE( negativeSum, denominator );        
    }

    public Vector computeParameterGradientAmalgamate(
        Collection<Object> partialResults )
    {
        RingAccumulator<Vector> numerator = new RingAccumulator<Vector>();
        double denominator = 0.0;
        for( Object result : partialResults )
        {
            GradientPartialSSE sse = (GradientPartialSSE) result;
            
            numerator.accumulate( sse.getFirst() );
            denominator += sse.getSecond();
        }
        
        Vector scaleSum = numerator.getSum();
        if( denominator != 0.0 )
        {
            scaleSum.scaleEquals( 1.0 / (2.0*denominator) );
        }
        return scaleSum;
    }
    
    
    @Override
    public Double evaluatePerformance(
        Collection<? extends TargetEstimatePair<Vector, Vector>> data )
    {
        double sumSquaredError = 0.0;
        double weightSum = 0.0;
        for (TargetEstimatePair<? extends Vector, ? extends Vector> pair : data)
        {
            // Compute the error vector.
            Vector target = pair.getTarget();
            Vector estimate = pair.getEstimate();
            double errorSquared = target.euclideanDistanceSquared( estimate );
            double weight = DatasetUtil.getWeight(pair);
            weightSum += weight;
            sumSquaredError += weight * errorSquared;
        }
        weightSum *= 2.0;

        if( weightSum == 0.0 )
        {
            return 0.0;
        }
        else
        {
            return sumSquaredError / weightSum;
        }
    }
    
    /**
     * Caches often-used values for the Cost Function
     */
    public static class Cache
        extends AbstractCloneableSerializable
    {
        /**
         * Jacobian
         */
        public final Matrix J;
        
        /**
         * Inner-product of the Jacobian matrix: J.transpose().times( J )
         */
        public final Matrix JtJ;
        
        /**
         * Jacobian transpose times Error: J.transpose().times( error )
         */
        public final Vector Jte;
        
        /**
         * Cost-function value of the parameter set
         */
        public final double parameterCost;
        
        /**
         * Creates a new instance of Cache
         * @param J
         * Jacobian
         * @param JtJ
         * Inner-product of the Jacobian matrix: J.transpose().times( J )
         * @param Jte
         * Jacobian transpose times Error: J.transpose().times( error )
         * @param parameterCost
         * Cost-function value of the parameter set
         */
        protected Cache(
            Matrix J,
            Matrix JtJ,
            Vector Jte,
            double parameterCost )
        {
            this.J = J;
            this.JtJ = JtJ;
            this.Jte = Jte;
            this.parameterCost = parameterCost;
        }
        
        
        /**
         * Computes often-used parameters of a sum-squared error term
         * @param objectToOptimize
         * GradientDescendable to compute the statistics of
         * @param data
         * Dataset to consider
         * @return
         * Cache containing the cached cost-function parameters
         */
        public static Cache compute(
            GradientDescendable objectToOptimize,
            Collection<? extends InputOutputPair<? extends Vector,Vector>> data )
        {
            RingAccumulator<Matrix> gradientAverage = new RingAccumulator<Matrix>();
            RingAccumulator<Vector> gradientError = new RingAccumulator<Vector>();
            
            // This is very close to the
            // MeanSquaredErrorCostFunction.computeParameterGradient() method
            double weightSum = 0.0;
            double parameterCost = 0.0;
            for (InputOutputPair<? extends Vector, ? extends Vector> pair : data)
            {
                // Compute the negativeError to save on Vector allocations
                // (can't use pair.getOutput because we'll alter the dataset)
                Vector negativeError = objectToOptimize.evaluate( pair.getInput() );
                negativeError.minusEquals( pair.getOutput() );
                double norm2 = negativeError.norm2Squared();

                double weight = DatasetUtil.getWeight(pair);
                if (weight != 1.0)
                {
                    negativeError.scaleEquals( weight );
                }
                weightSum += weight;

                parameterCost += norm2 * weight;

                Matrix gradient = 
                    objectToOptimize.computeParameterGradient( pair.getInput() );
                gradientAverage.accumulate( gradient );

                gradientError.accumulate( negativeError.times( gradient ) );
            }
        
            weightSum *= 2.0;
            if( weightSum == 0.0 )
            {
                weightSum = 1.0;
            }
            
            // This is the Jacobian
            Matrix J = gradientAverage.getSum();
            J.scaleEquals( 1.0 / weightSum );
            Matrix JtJ = J.transpose().times( J );
            
            // Have to use 1.0 here because we've been accumulating the 
            // negativeError to save Vector allocations and the chain rule
            // brings down the 2.0 from the exponent and we're already
            // hitting the function with 0.5, so it's a wash.
            Vector Jte = gradientError.getSum();
            Jte.scaleEquals( 1.0 / weightSum );
            
            // Make sure the cost is normalized by the weights
            parameterCost /= weightSum;
            
            return new Cache( J, JtJ, Jte, parameterCost );
        }
        
    }
    
    
    /**
     * Partial result from the SSE evaluate computation
     */
    private static class EvaluatePartialSSE
        extends DefaultPair<Double,Double>
    {

        /**
         * Creates a new instance of EvaluatePartialSSE
         * @param numerator
         * Numerator
         * @param denominator
         * Denominator
         */
        public EvaluatePartialSSE(
            Double numerator,
            Double denominator )
        {
            super( numerator, denominator );
        }
        
    }
    
    /**
     * Partial result from the SSE gradient computation
     */
    public static class GradientPartialSSE
        extends DefaultPair<Vector,Double>
    {
        /**
         * Creates a new instance of GradientPartialSSE
         * @param numerator
         * Numerator
         * @param denominator
         * Denominator
         */        
        public GradientPartialSSE(
            Vector numerator,
            Double denominator )
        {
            super( numerator, denominator );
        }
    }
    
    

}
