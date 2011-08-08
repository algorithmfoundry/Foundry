/*
 * File:                RootFinder.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 5, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.root;

import gov.sandia.cognition.algorithm.AnytimeAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * Defines the functionality of a root-finding algorithm.  That is, an
 * algorithm that finds a solutions of a scalar function where it equals zero,
 * find "x" such that f(x)=0, also known as the zero crossing.  These
 * algorithms are generally iterative and approximate, and thus have a
 * tunable tolerance parameter.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Root-finding algorithm",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Root-finding_algorithm"
)
public interface RootFinder 
    extends BatchLearner<Evaluator<Double,Double>, InputOutputPair<Double,Double>>,
    AnytimeAlgorithm<InputOutputPair<Double,Double>>
{

    /**
     * Sets the initial guess of the root (zero-crossing), which is supplied
     * as input to the function to find the zero-crossings of.
     * @return
     * Initial guess of the root location.
     */
    public double getInitialGuess();

    /**
     * Sets the initial guess of the root (zero-crossing), which is supplied
     * as input to the function to find the zero-crossings of.
     * @param intitialGuess
     * Initial guess of the root location.
     */
    public void setInitialGuess(
        double intitialGuess );
    
    /**
     * Gets the tolerance of the algorithm.
     * @return
     * Tolerance, where tolerances closer to zero are more accurate, and larger
     * tolerances are less accurate.  In any case, tolerance must be greater
     * than or equal to zero.
     */
    public double getTolerance();

    /**
     * Sets the tolerance of the algorithm.  Smaller tolerances may yield more
     * accurate estimates but spend more computation finding them.
     * @param tolerance
     * Tolerance, where tolerances closer to zero are more accurate, and larger
     * tolerances are less accurate.  In any case, tolerance must be greater
     * than or equal to zero.
     */
    public void setTolerance(
        double tolerance );
    
}
