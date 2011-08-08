/*
 * File:                LineBracketInterpolator.java
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
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Definition of an interpolator/extrapolator for a LineBracket.  A
 * LineBracketInterpolator takes the points contained in a LineBracket and
 * fits a computationally efficient function to those points
 * (such as a parabola, straight line, etc.).  The interpolating function is
 * then used to find a hypothesized minimum on a required interval.
 * 
 * @param <EvaluatorType> Type of Evaluator to use
 * @author Kevin R. Dixon
 * @since 2.1
 */
public interface LineBracketInterpolator<EvaluatorType extends Evaluator<Double,Double>> 
    extends CloneableSerializable
{

    /**
     * Gets the tolerance of the interpolator to collinear or identical points,
     * typically 1e-6, must be greater than 0.0.
     * @return
     * Tolerance of the interpolator
     */
    public double getTolerance();
    
    /**
     * Finds the minimum of the bracket using the interpolation/extrapolation
     * routine, where the minimum must lie between the minx and maxx values on
     * the x-axis.
     * @param bracket
     * Bracket of points
     * @param minx
     * Minimum x-axis value to search for the minimum
     * @param maxx
     * Maximum x-axis value to search for the minimum
     * @param function
     * Function to consider.  The function, or gradient, may be evaluated to
     * generate enough output/slope samples to interpolate.
     * @return
     * Minimum value of the function between minx and maxx.
     */
    public double findMinimum(
        LineBracket bracket,
        double minx,
        double maxx,
        EvaluatorType function );
    
    /**
     * Determines if the LineBracket contains a sufficient number/quality of
     * points to conduct the interpolation.
     * @param bracket
     * LineBracket to consider
     * @return
     * true if the LineBracket has a sufficient number of points, false if
     * the LineBracket doesn't have enough.
     */
    public boolean hasSufficientPoints(
        LineBracket bracket );
    
}
