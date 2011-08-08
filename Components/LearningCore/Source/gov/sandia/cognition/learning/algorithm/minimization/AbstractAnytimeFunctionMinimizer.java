/*
 * File:                AbstractStandardIterativeMinimizationAlgorithm.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * A partial implementation of a minimization algorithm that is iterative,
 * stoppable, and approximate.
 *
 * @param <InputType> 
 * Input class of the Evaluator that we are trying to minimize, such as Vector
 * @param <OutputType> 
 * Output class of the Evaluator that we are trying to minimize, such as Double
 * @param <EvaluatorType> 
 * Evaluator class that this minimization algorithm can handle, such as
 * Evaluator or DifferentiableEvaluator.
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public abstract class AbstractAnytimeFunctionMinimizer<InputType, OutputType, EvaluatorType extends Evaluator<? super InputType, ? extends OutputType>>
    extends AbstractAnytimeBatchLearner<EvaluatorType, InputOutputPair<InputType, OutputType>>
    implements FunctionMinimizer<InputType, OutputType, EvaluatorType>
{

    /**
     * Tolerance of the minimization algorithm, must be >= 0.0
     */
    protected double tolerance;

    /**
     * Resulting minimum input-output pair
     */
    protected InputOutputPair<InputType, OutputType> result;

    /**
     * Initial guess of the minimization routine
     */
    protected InputType initialGuess;

    /**
     * Creates a new instance of AbstractStandardIterativeMinimizationAlgorithm
     * @param initialGuess 
     * Initial guess of the minimization routine
     * @param tolerance 
     * Tolerance of the minimization algorithm, must be >= 0.0
     * @param maxIterations 
     * Maximum number of iterations to run before stopping
     */
    public AbstractAnytimeFunctionMinimizer(
        InputType initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( maxIterations );
        this.setTolerance( tolerance );
        this.setInitialGuess( initialGuess );
        this.setResult( null );
    }

    /**
     * Getter for result
     * @return 
     * Resulting minimum input-output pair
     */
    public InputOutputPair<InputType, OutputType> getResult()
    {
        return this.result;
    }

    /**
     * Setter for result
     * @param result 
     * Resulting minimum input-output pair
     */
    protected void setResult(
        InputOutputPair<InputType, OutputType> result )
    {
        this.result = result;
    }

    /**
     * Getter for tolerance
     * @return 
     * Tolerance of the minimization algorithm, must be >= 0.0
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance 
     * Tolerance of the minimization algorithm, must be >= 0.0
     */
    public void setTolerance(
        double tolerance )
    {
        if (tolerance < 0.0)
        {
            throw new IllegalArgumentException(
                "Tolerance must be >= 0.0" );
        }
        this.tolerance = tolerance;
    }

    /**
     * Getter for initialGuess 
     * @return 
     * Initial guess of the minimization routine
     */
    public InputType getInitialGuess()
    {
        return this.initialGuess;
    }

    /**
     * Setter for initialGuess
     * @param initialGuess 
     * Initial guess of the minimization routine
     */
    public void setInitialGuess(
        InputType initialGuess )
    {
        this.initialGuess = initialGuess;
    }

}
