/*
 * File:                AbstractLogisticRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 11, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Abstract partial implementation for logistic regression classes.
 * @param <InputType>
 * Type of input using the regression
 * @param <OutputType>
 * Type of outputs from the regression
 * @param <FunctionType>
 * TYpe of Evaluator that maps the InputType to the OutputType
 * @author Kevin R. Dixon
 * @since 3.3.3
 */
public abstract class AbstractLogisticRegression<InputType,OutputType,FunctionType extends Evaluator<? super InputType,OutputType>>
    extends AbstractAnytimeSupervisedBatchLearner<InputType,OutputType,FunctionType>
{

    /**
     * Default number of iterations before stopping, {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /**
     * Default tolerance change in weights before stopping, {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-10;

    /**
     * Default regularization, {@value}.
     */
    public static final double DEFAULT_REGULARIZATION = 0.0;

    /**
     * The object to optimize, used as a factory on successive runs of the
     * algorithm.
     */
    protected FunctionType objectToOptimize;

    /**
     * Return value from the algorithm
     */
    protected FunctionType result;

    /**
     * Tolerance change in weights before stopping
     */
    protected double tolerance;

    /**
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    protected double regularization;


    /**
     * Creates a new instance of AbstractLogisticRegression
     * @param regularization
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     * @param tolerance
     * Tolerance change in weights before stopping
     * @param maxIterations
     * Maximum number of iterations before stopping
     */
    public AbstractLogisticRegression(
        double regularization,
        double tolerance,
        int maxIterations )
    {
        super( maxIterations );
        this.setRegularization(regularization);
        this.setTolerance( tolerance );
    }

    @Override
    public AbstractLogisticRegression<InputType,OutputType,FunctionType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractLogisticRegression<InputType,OutputType,FunctionType> clone =
            (AbstractLogisticRegression<InputType,OutputType,FunctionType>) super.clone();
        clone.setObjectToOptimize(
            ObjectUtil.cloneSmart( this.getObjectToOptimize() ) );
        clone.result = ObjectUtil.cloneSmart( this.getResult() );
        return clone;
    }

    /**
     * Getter for objectToOptimize
     * @return
     * The object to optimize, used as a factory on successive runs of the
     * algorithm.
     */
    public FunctionType getObjectToOptimize()
    {
        return this.objectToOptimize;
    }

    /**
     * Setter for objectToOptimize
     * @param objectToOptimize
     * The object to optimize, used as a factory on successive runs of the
     * algorithm.
     */
    public void setObjectToOptimize(
        FunctionType objectToOptimize )
    {
        this.objectToOptimize = objectToOptimize;
    }

    @Override
    public FunctionType getResult()
    {
        return this.result;
    }

    /**
     * Getter for tolerance
     * @return
     * Tolerance change in weights before stopping, must be nonnegative.
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Tolerance change in weights before stopping, must be nonnegative.
     */
    public void setTolerance(
        double tolerance )
    {
        ArgumentChecker.assertIsNonNegative("tolerance", tolerance);
        this.tolerance = tolerance;
    }

    /**
     * Getter for regularization
     * @return 
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    public double getRegularization()
    {
        return this.regularization;
    }

    /**
     * Setter for regularization
     * @param regularization
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    public void setRegularization(
        double regularization)
    {
        ArgumentChecker.assertIsNonNegative("regularization", regularization);
        this.regularization = regularization;
    }

}
