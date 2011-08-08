/*
 * File:                LineBracketInterpolatorBrent.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line.interpolator;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;

/**
 * Implements Brent's method of function interpolation to find a minimum.
 * If the function is well behaved, then Brent uses non-slope-based parabolic
 * interpolation.  Otherwise, Brent uses Golden-section and secant
 * interpolation.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class LineBracketInterpolatorBrent 
    extends AbstractLineBracketInterpolator<Evaluator<Double,Double>>
{

    /**
     * Non-slope based parabolic interpolator.
     */
    private LineBracketInterpolatorParabola parabolicInterpolator;
    
    /**
     * Golden-section step interpolator.
     */
    private LineBracketInterpolatorGoldenSection goldenInterpolator;
    
    /** 
     * Creates a new instance of LineBracketInterpolatorBrent 
     */
    public LineBracketInterpolatorBrent()
    {
        super( DEFAULT_TOLERANCE );
        this.setGoldenInterpolator( new LineBracketInterpolatorGoldenSection() );
        this.setParabolicInterpolator( new LineBracketInterpolatorParabola() );
    }
    
    public double findMinimum(
        LineBracket bracket,
        double minx,
        double maxx,
        Evaluator<Double, Double> function )
    {
        
        // We'd prefer using the parabolic interpolator, but only if
        // 1) We have enough points
        // 2) The function is well behaved
        // If these conditions aren't met, then use the Golden-Section step
        double nextX = Double.POSITIVE_INFINITY;
        
        boolean useGoldenSection;
        
        if( this.getParabolicInterpolator().hasSufficientPoints( bracket ) )
        {
            try
            {
                nextX = this.getParabolicInterpolator().findMinimum( 
                    bracket, minx, maxx, function );
                useGoldenSection = false;
            }
            // Parabolic interpolation failed... using Golden Section step instead.
            catch (IllegalArgumentException e)
            {
                useGoldenSection = true;
            }
        }
        else
        {
            useGoldenSection = true;
        }
        
        // If parabolic interpolation wasn't appropriate, then just use the
        // golden section step
        if( useGoldenSection )
        {
            nextX = this.getGoldenInterpolator().findMinimum( bracket, minx, maxx, function );
        }
        
        return nextX;
        
    }

    public boolean hasSufficientPoints(
        LineBracket bracket )
    {
        return (bracket.getLowerBound() != null) &&
            (bracket.getUpperBound() != null);
    }

    /**
     * Getter for parabolicInterpolator
     * @return
     * Non-slope based parabolic interpolator.
     */
    public LineBracketInterpolatorParabola getParabolicInterpolator()
    {
        return this.parabolicInterpolator;
    }

    /**
     * Setter for parabolicInterpolator
     * @param parabolicInterpolator
     * Non-slope based parabolic interpolator.
     */
    public void setParabolicInterpolator(
        LineBracketInterpolatorParabola parabolicInterpolator )
    {
        this.parabolicInterpolator = parabolicInterpolator;
    }

    /**
     * Getter for goldenInterpolator
     * @return
     * Golden-section step interpolator.
     */
    public LineBracketInterpolatorGoldenSection getGoldenInterpolator()
    {
        return this.goldenInterpolator;
    }

    /**
     * Setter for goldenInterpolator
     * @param goldenInterpolator
     * Golden-section step interpolator.
     */
    public void setGoldenInterpolator(
        LineBracketInterpolatorGoldenSection goldenInterpolator )
    {
        this.goldenInterpolator = goldenInterpolator;
    }

}
