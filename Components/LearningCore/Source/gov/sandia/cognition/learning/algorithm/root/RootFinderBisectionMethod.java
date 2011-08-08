/*
 * File:                RootFinderBisectionMethod.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;

/**
 * Bisection algorithm for root finding.  This works sort of like a "binary
 * search" algorithm, by halving the possible region where the root could be
 * by half each iteration.  This algorithm is guaranteed to find the root
 * if given a valid bracket. However, it tends to be the slowest algorithm
 * for root finding.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Bisection method",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Bisection_method"
)
public class RootFinderBisectionMethod 
    extends AbstractBracketedRootFinder
{
    
    /** 
     * Creates a new instance of RootFinderBisectionMethod 
     */
    public RootFinderBisectionMethod()
    {
        super();
    }

    @Override
    protected boolean step()
    {
        double xa = this.getRootBracket().getLowerBound().getInput();
        double delta = this.getRootBracket().getUpperBound().getInput() - xa;
        double xmid = xa + 0.5*delta;
        double ymid = this.data.evaluate( xmid );
        
        InputOutputSlopeTriplet midpoint =
            new InputOutputSlopeTriplet( xmid, ymid, null );
        this.getRootBracket().setOtherPoint( midpoint );

        if( ymid == 0.0 )
        {
            return false;
        }

        // The midpoint output has same sign as left bracket, so throw
        // away the left half of the interval.
        if( this.getRootBracket().getLowerBound().getOutput() * ymid > 0.0 )
        {
            this.getRootBracket().setLowerBound( midpoint );
        }
        // Otherwise, the midpoint must have the same sign as the right bracket,
        // so throw away the right half of the interval.
        else
        {
            this.getRootBracket().setUpperBound( midpoint );
        }
        
        return delta > this.getTolerance();
        
    }
    
}
