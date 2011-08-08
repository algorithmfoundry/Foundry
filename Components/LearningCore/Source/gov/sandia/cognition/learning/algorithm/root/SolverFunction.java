/*
 * File:                SolverFunction.java
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
import gov.sandia.cognition.math.AbstractUnivariateScalarFunction;

/**
 * Evaluator that allows RootFinders to solve for nonzero values by setting
 * a "target" parameter.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class SolverFunction 
    extends AbstractUnivariateScalarFunction
{

    /**
     * internalFunction value to search for.
     */
    private double target;

    /**
     * The internal function to use.
     */
    private Evaluator<Double,Double> internalFunction;

    /** 
     * Creates a new instance of SolverFunction 
     */
    public SolverFunction()
    {
        this( 0.0, null );
    }

    /**
     * Creates a new instance of SolverFunction
     *
     * @param target
     * internalFunction value to search for.
     * @param internalFunction
     * The internal function to use.
     */
    public SolverFunction(
        double target,
        Evaluator<Double, Double> internalFunction )
    {
        this.setTarget(target);
        this.setInternalFunction(internalFunction);
        this.target = target;
        this.internalFunction = internalFunction;
    }

    /**
     * Getter for target
     * @return 
     * internalFunction value to search for.
     */
    public double getTarget()
    {
        return this.target;
    }

    /**
     * Setter for target
     * @param target
     * internalFunction value to search for.
     */
    public void setTarget(
        double target)
    {
        this.target = target;
    }

    /**
     * Getter for internalFunction
     * @return
     * The internal function to use.
     */
    public Evaluator<Double, Double> getInternalFunction()
    {
        return this.internalFunction;
    }

    /**
     * Setter for internalFunction.
     * @param internalFunction
     * The internal function to use.
     */
    public void setInternalFunction(
        Evaluator<Double, Double> internalFunction)
    {
        this.internalFunction = internalFunction;
    }

    public double evaluate(
        double input)
    {
        return this.internalFunction.evaluate(input) - this.target;
    }
    
}
