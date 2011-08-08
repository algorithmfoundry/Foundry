/*
 * File:                MinimizationStoppingCriterion.java
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

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.math.matrix.Vector;

/**
 * Implementation of almost zero-gradient convergence test for function
 * minimizers.
 * @author Kevin R. Dixon
 * @since 2.1
 */
public class MinimizationStoppingCriterion 
{

    /**
     * Test for convergence on change in x, {@value}
     */
    private static final double TOLERANCE_DELTA_X = 1e-7;    
    
    /**
     * Tests for convergence on approximately zero slope and nonmovement along
     * the x-axis
     * @param xnew
     * Present x-axis value
     * @param fxnew 
     * Present y-value at xnew, may be null if unknown
     * @param gradient
     * Gradient at the new point
     * @param delta
     * Change in x-axis between iterations
     * @param tolerance
     * Tolerance of the stopping criterion, typically ~1e-5
     * @return
     * True if converged, false otherwise
     */
    public static boolean convergence(
        Vector xnew,
        Double fxnew,
        Vector gradient,
        Vector delta,
        double tolerance )
    {
        
        // See if we've converged on zero slope
        double maximumDelta = 0.0;
        double maximumGradient = 0.0;
        double gradDenom = (fxnew==null) ? 1.0 : Math.max( fxnew, 1.0 );
        for( int i = 0; i < xnew.getDimensionality(); i++ )
        {
            // Normalizing coefficient: max(|xi|, 1.0), so that we're always
            // reducing the values
            double normalizedX = Math.max( Math.abs(xnew.getElement(i)), 1.0 );
            double deltaX = Math.abs(delta.getElement(i)) / normalizedX;
            if( maximumDelta < deltaX )
            {
                maximumDelta = deltaX;
            }
            
            double gradX = Math.abs(gradient.getElement(i)) * normalizedX / gradDenom;
            if( maximumGradient < gradX )
            {
                maximumGradient = gradX;
            }
            
        }
        
        // This is the normal stopping criteria
        if( (maximumDelta < TOLERANCE_DELTA_X) ||
            (maximumGradient < tolerance) )
        {
            return true;
        }
        else
        {
            return false;
        }   
        
    }
    
}
