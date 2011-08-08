/*
 * File:                AbstractLineBracketInterpolatorPolynomial.java
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
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;

/**
 * Partial implementation of a LineBracketInterpolator based on a closed-form
 * polynomial function.
 * @param <EvaluatorType> Type of Evaluator to consider
 * @author Kevin R. Dixon
 * @since 2.2
 */
public abstract class AbstractLineBracketInterpolatorPolynomial<EvaluatorType extends Evaluator<Double,Double>>
    extends AbstractLineBracketInterpolator<EvaluatorType>
{

    /** 
     * Creates a new instance of AbstractLineBracketInterpolatorPolynomial 
     * @param tolerance 
     * Collinear tolerance of the algorithm.
     */
    public AbstractLineBracketInterpolatorPolynomial(
        double tolerance )
    {
        super( tolerance );
    }

    public double findMinimum(
        LineBracket bracket,
        double minx,
        double maxx,
        EvaluatorType function )
    {
        
        // Compute the interpolating polynomial first
        PolynomialFunction.ClosedForm interpolator =
            this.computePolynomial( bracket, function );
        
        //Find the optimal stationary point within the required interval
        double fstar = Double.POSITIVE_INFINITY;
        double xstar = 0.0;
        Double[] stationaryPoints = interpolator.stationaryPoints();
        for( int i = 0; i < stationaryPoints.length; i++ )
        {
            double x = stationaryPoints[i];
            if( (minx <= x) && (x <= maxx) )
            {
                double f = interpolator.evaluate( x );
                if( fstar > f )
                {
                    fstar = f;
                    xstar = x;
                }
            }
            
        }
        
        // Find the lowest-value bound on the interval
        double bestBoundx;
        double bestBoundfx;
        double fminx = interpolator.evaluate( minx );
        double fmaxx = interpolator.evaluate( maxx );
        if( fminx < fmaxx )
        {
            bestBoundx = minx;
            bestBoundfx = fminx;
        }
        else
        {
            bestBoundx = maxx;
            bestBoundfx = fmaxx;
        }
        
        // return the best of the stationary points OR the interval bound
        double bestx;
        if( fstar <= bestBoundfx )
        {
            bestx = xstar;
        }
        else
        {
            bestx = bestBoundx;
        }
        
        return bestx;
        
    }
    
    /**
     * Fits the interpolating polynomial to the given LineBracket
     * @param bracket
     * LineBracket to consider
     * @param function
     * Function to use to fill in missing information
     * @return
     * Interpolating polynomial
     */
    public abstract PolynomialFunction.ClosedForm computePolynomial(
        LineBracket bracket,
        EvaluatorType function );

}
