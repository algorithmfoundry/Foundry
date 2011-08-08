/*
 * File:                MinimizerBasedRootFinder.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 9, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.root;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.algorithm.AnytimeAlgorithmWrapper;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineMinimizerDerivativeFree;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;

/**
 * A root finder that uses minimization techniques to find the roots
 * (zero-crossings).
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class MinimizerBasedRootFinder 
    extends AnytimeAlgorithmWrapper<InputOutputPair<Double, Double>,LineMinimizer<Evaluator<Double,Double>>>
    implements RootFinder
{

    /**
     * Internal function.
     */
    private MinimizationFunction internalFunction;

    /** 
     * Creates a new instance of MinimizerBasedRootFinder 
     */
    public MinimizerBasedRootFinder()
    {
        this( new LineMinimizerDerivativeFree() );
    }

    /**
     * Creates a new instance of MinimizerBasedRootFinder
     * @param algorithm
     * Minimization algorithm.
     */
    public MinimizerBasedRootFinder(
        LineMinimizer<Evaluator<Double,Double>> algorithm )
    {
        super( algorithm );
        this.setInitialGuess( 0.0 );
        this.setTolerance(1e-5);
        this.internalFunction = null;
    }

    public InputOutputPair<Double, Double> getResult()
    {
        if( (this.internalFunction == null) ||
            (this.getAlgorithm().getResult() == null) )
        {
            return null;
        }
        else
        {
            Double x = this.getAlgorithm().getResult().getInput();
            return new DefaultInputOutputPair<Double, Double>(
                x, this.internalFunction.function.evaluate(x) );
        }
    }

    public double getInitialGuess()
    {
        return this.getAlgorithm().getInitialGuess();
    }

    public void setInitialGuess(
        double intitialGuess)
    {
        this.getAlgorithm().setInitialGuess( intitialGuess );
    }

    public void setTolerance(
        double tolerance)
    {
        this.getAlgorithm().setTolerance(tolerance);
    }

    public double getTolerance()
    {
        return this.getAlgorithm().getTolerance();
    }
    
    public InputOutputPair<Double, Double> learn(
        Evaluator<Double, Double> data)
    {
        this.internalFunction = new MinimizationFunction( data, 0.0 );
        this.getAlgorithm().learn( this.internalFunction );
        return this.getResult();
    }

    /**
     * Minimization function that returns the quadratic error to the given
     * target.
     */
    private static class MinimizationFunction
        extends AbstractUnivariateScalarFunction
    {

        /**
         * Function that we are searching against
         */
        private Evaluator<Double, Double> function;

        /**
         * Solution we are seeking in the target
         */
        private double functionTarget;

        /**
         * Creates a new instance of MinizationFunction
         * @param function
         * Function that we are searching against
         * @param functionTarget
         * Solution we are seeking in the target
         */
        public MinimizationFunction(
            Evaluator<Double, Double> function,
            double functionTarget )
        {
            this.function = function;
            this.functionTarget = functionTarget;
        }

        /**
         * Returns the quadratic error of the input, in other words
         * (ytarget-f(input))^2
         * @param input
         * Input to put into the Evaluator
         * @return quadratic error of the input w.r.t. the output target
         */
        public double evaluate(
            double input )
        {
            double delta = this.functionTarget - this.function.evaluate( input );
            return delta * delta;
        }

    }

}
