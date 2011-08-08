/*
 * File:                AbstractLineBracketInterpolator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line.interpolator;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Partial implementation of LinearBracketInterpolator
 * @param <EvaluatorType> Type of Evaluator that this interpolator can use
 * @author Kevin R. Dixon
 * @since 2.1
 */
public abstract class AbstractLineBracketInterpolator<EvaluatorType extends Evaluator<Double,Double>>
    extends AbstractCloneableSerializable
    implements LineBracketInterpolator<EvaluatorType>
{

    /**
     * Default collinearity or identity tolerance, {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-6;
    
    /**
     * Tolerance of the interpolator to collinear or identical points
     */
    private double tolerance;
    
    /**
     * Default constructor
     */
    public AbstractLineBracketInterpolator()
    {
        this( DEFAULT_TOLERANCE );
    }
    
    /** 
     * Creates a new instance of AbstractLineBracketInterpolator 
     * @param tolerance 
     * Tolerance of the interpolator to collinear or identical points
     */
    public AbstractLineBracketInterpolator(
        double tolerance )
    {
        this.setTolerance( tolerance );
    }

    public double getTolerance()
    {
        return tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Tolerance of the interpolator to collinear or identical points
     */
    public void setTolerance(
        double tolerance )
    {
        if( tolerance <= 0.0 )
        {
            throw new IllegalArgumentException(
                "Tolerance must be > 0.0" );
        }
        this.tolerance = tolerance;
    }


}
