/*
 * File:                RootBracketer.java
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
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;

/**
 * Defines the functionality of a algorithm that finds a bracket of a root
 * from an initial guess.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface RootBracketer
    extends BatchLearner<Evaluator<Double,Double>, LineBracket>,
    AnytimeAlgorithm<LineBracket>
{
    
    /**
     * Sets the initial guess of the root location.
     * @param intitialGuess
     * Initial guess of the root location.
     */
    public void setInitialGuess(
        double intitialGuess );
    
}
