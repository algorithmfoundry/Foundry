/*
 * File:                WolfeConditions.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 6, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The Wolfe conditions define a set of sufficient conditions for
 * "sufficient decrease" in inexact line search.  These consist of two "anded"
 * conditions: The Goldstein condition specifying sufficient decrease,
 * sometimes called the "Armijo condition", and the curvature condition
 * ensuring that a trial point is sufficiently steep.  The Wolfe conditions
 * are sometimes called the "Wolfe-Powell conditions".
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="R. Fletcher",
            title="Practical Methods of Optimization, Second Edition",
            type=PublicationType.Book,
            year=1987,
            pages={26,30},
            notes={
                "Equation 2.5.1 and Equation 2.5.6",
                "Fletcher assumes the initial point has negative slope."
            }
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Wolfe conditions",
            url="http://en.wikipedia.org/wiki/Wolfe_conditions",
            type=PublicationType.WebPage,
            year=2008
        )
    }
)
public class WolfeConditions
    extends AbstractCloneableSerializable
{

    /**
     * Original point to store, slope must be less than 0.0.
     */
    private InputOutputSlopeTriplet originalPoint;
        
    /**
     * Slope condition parameter for the Goldstein condition, must be less than 
     * curvatureCondition and on the interval (0,1).
     */
    private double slopeCondition;
    
    /**
     * Curvature condition for the curvature condition, must be greater than
     * slopeCondition and on the interval (0,1).
     */
    private double curvatureCondition;
        
    /** 
     * Creates a new instance of WolfeConditions 
     * 
     * @param originalPoint
     * Original point to store, slope must be less than 0.0.
     * @param slopeCondition
     * Slope condition parameter for the Goldstein condition, must be less than 
     * curvatureCondition and on the interval (0,1).
     * @param curvatureCondition
     * Curvature condition for the curvature condition, must be greater than
     * slopeCondition and on the interval (0,1).
     */
    public WolfeConditions(
        InputOutputSlopeTriplet originalPoint,
        double slopeCondition,
        double curvatureCondition )
    {
        if( slopeCondition >= curvatureCondition )
        {
            throw new IllegalArgumentException(
                "slopeCondition must be strictly less than curvatureCondition" );
        }
        
        if( originalPoint.getSlope() >= 0.0 )
        {
            throw new IllegalArgumentException(
                "Can only use Wolfe conditions when original slope < 0.0" );
        }
        
        this.setOriginalPoint( originalPoint );
        this.setSlopeCondition( slopeCondition );
        this.setCurvatureCondition( curvatureCondition );
    }

    /**
     * Copy Constructor
     * @param other WolfeConditions to copy
     */
    public WolfeConditions(
        WolfeConditions other )
    {
        this( ObjectUtil.cloneSafe(other.getOriginalPoint()),
            other.getSlopeCondition(), 
            other.getCurvatureCondition() );
    }
    
    @Override
    public WolfeConditions clone()
    {
        WolfeConditions clone = (WolfeConditions) super.clone();
        clone.setOriginalPoint( ObjectUtil.cloneSafe( this.getOriginalPoint() ) );
        return clone;
    }

    /**
     * Evaluates if the trial point meets the Wolfe conditions
     * @param trialPoint
     * Trial point to consider
     * @return
     * True if Wolfe conditions for sufficient decrease are satisfied, false if
     * either the Goldstein or strict curvature conditions are not satisfied.
     */
    public boolean evaluate(
        InputOutputSlopeTriplet trialPoint )
    {
        return this.evaluateGoldsteinCondition( trialPoint ) &&
            this.evaluateStrictCurvatureCondition( trialPoint.getSlope() );
    }
    
    /**
     * Evaluates the Goldstein (Armijo) conditions, which is purely a
     * sufficient decrease condition.
     * @param trialPoint
     * Trial point to consider.
     * @return
     * True if the Goldstein condition is satisfied, false otherwise.
     */
    public boolean evaluateGoldsteinCondition(
        InputOutputPair<Double,Double> trialPoint )
    {
        return WolfeConditions.evaluateGoldsteinCondition( 
            this.getOriginalPoint(), trialPoint, this.getSlopeCondition() );
    }
    
    /**
     * Evaluates the Goldstein (Armijo) conditions, which is purely a
     * sufficient decrease condition.
     * 
     * @param originalPoint
     * Original point to store, slope must be less than 0.0.
     * @param trialPoint
     * Trial point to consider.
     * @param slopeCondition
     * Slope condition parameter for the Goldstein condition, must be less than 
     * curvatureCondition and on the interval (0,1).
     * @return
     * True if the Goldstein condition is satisfied, false otherwise.
     */
    @PublicationReference(
        author="R. Fletcher",
        title="Practical Methods of Optimization, Second Edition",
        type=PublicationType.Book,
        year=1987,
        pages=27,
        notes="Equation 2.5.1"
    )
    public static boolean evaluateGoldsteinCondition(
        InputOutputSlopeTriplet originalPoint,
        InputOutputPair<Double,Double> trialPoint,
        double slopeCondition )
    {
        double delta = trialPoint.getInput() - originalPoint.getInput();
        return trialPoint.getOutput() <= 
            originalPoint.getOutput() + delta*slopeCondition*originalPoint.getSlope();        
    }
    
    /**
     * Evaluates the strict curvature condition.
     * @param trialSlope
     * Trial slope to consider.
     * @return
     * True if the strict curvature condition is satisfied, false otherwise.
     */
    public boolean evaluateStrictCurvatureCondition(
        double trialSlope )
    {
        return WolfeConditions.evaluateStrictCurvatureCondition( 
            this.getOriginalPoint().getSlope(), trialSlope, this.getCurvatureCondition() );
    }
    
    /**
     * Evaluates the strict curvature condition.
     * 
     * @param originalSlope
     * Slope at the original point.
     * @param trialSlope
     * Trial slope to consider.
     * @param curvatureCondition
     * Curvature condition for the curvature condition, must be greater than
     * slopeCondition and on the interval (0,1).
     * @return
     * True if the strict curvature condition is satisfied, false otherwise.
     */
    @PublicationReference(
        author="R. Fletcher",
        title="Practical Methods of Optimization, Second Edition",
        type=PublicationType.Book,
        year=1987,
        pages=29,
        notes="Equation 2.5.6"
    )
    public static boolean evaluateStrictCurvatureCondition(
        double originalSlope,
        double trialSlope,
        double curvatureCondition )
    {
        if( originalSlope >= 0.0 )
        {
            throw new IllegalArgumentException(
                "Original slope must be < 0.0" );
        }
        
        // Fletcher assumes that the originalSlope is < 0.0
        return Math.abs( trialSlope ) <= -curvatureCondition * originalSlope;
    }
    
    /**
     * Getter for originalPoint
     * @return
     * Original point to store, slope must be less than 0.0.
     */
    public InputOutputSlopeTriplet getOriginalPoint()
    {
        return this.originalPoint;
    }

    /**
     * Setter for originalPoint
     * @param originalPoint
     * Original point to store, slope must be less than 0.0.
     */
    public void setOriginalPoint(
        InputOutputSlopeTriplet originalPoint )
    {
        this.originalPoint = originalPoint;
    }

    /**
     * Getter for slopeCondition
     * @return
     * Slope condition parameter for the Goldstein condition, must be less than 
     * curvatureCondition and on the interval (0,1).
     */
    public double getSlopeCondition()
    {
        return this.slopeCondition;
    }

    /**
     * Setter for slopeCondition
     * @param slopeCondition
     * Slope condition parameter for the Goldstein condition, must be less than 
     * curvatureCondition and on the interval (0,1).
     */
    public void setSlopeCondition(
        double slopeCondition )
    {
        if( (slopeCondition <= 0.0) || (slopeCondition >= 1.0) )
        {
            throw new IllegalArgumentException(
                "slopeCondition must be on the interval (0,1)" );
        }
        this.slopeCondition = slopeCondition;
    }

    /**
     * Getter for curvatureCondition
     * @return
     * Curvature condition for the curvature condition, must be greater than
     * slopeCondition and on the interval (0,1).
     */
    public double getCurvatureCondition()
    {
        return this.curvatureCondition;
    }

    /**
     * Setter for curvatureCondition
     * @param curvatureCondition
     * Curvature condition for the curvature condition, must be greater than
     * slopeCondition and on the interval (0,1).
     */
    public void setCurvatureCondition(
        double curvatureCondition )
    {
        if( (curvatureCondition <= 0.0) || (curvatureCondition >= 1.0) )
        {
            throw new IllegalArgumentException(
                "curvatureCondition must be on the interval (0,1)" );
        }
        this.curvatureCondition = curvatureCondition;
    }
    
}
