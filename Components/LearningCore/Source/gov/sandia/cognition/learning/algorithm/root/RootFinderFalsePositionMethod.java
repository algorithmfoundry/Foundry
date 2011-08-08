/*
 * File:                RootFinderFalsePositionMethod.java
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
 * The false-position algorithm for root finding.  This algorithm is guaranteed
 * to find a root if given a valid bracket.  The algorithm sort of works like
 * the secant method, but maintains a rigorous bracket on a root at all times.
 * It is generally faster than bisection, but slower than Ridders's and the
 * secant method.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="False position method",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/False_position_method"
)
public class RootFinderFalsePositionMethod 
    extends AbstractBracketedRootFinder
{

    /** 
     * Creates a new instance of RootFinderFalsePositionMethod 
     */
    public RootFinderFalsePositionMethod()
    {
        super();
    }

    @Override
    protected boolean step()
    {
        
        double xa = this.getRootBracket().getLowerBound().getInput();
        double fa = this.getRootBracket().getLowerBound().getOutput();
        
        double xb = this.getRootBracket().getUpperBound().getInput();
        double fb = this.getRootBracket().getUpperBound().getOutput();
        
        // This is the root of the secant
        double xsecant = (fb*xa - fa*xb) / (fb-fa);
        double fsecant = this.data.evaluate( xsecant );
        InputOutputSlopeTriplet secantRoot = new InputOutputSlopeTriplet(
            xsecant, fsecant, null );
        this.getRootBracket().setOtherPoint( secantRoot );

        if( fsecant == 0.0 )
        {
            return false;
        }

        // if fa and fsecant have the same sign, then throw away the
        // lower bound and replace it with the root of the secant.
        double delta;
        if( fa * fsecant > 0.0 )
        {
            delta = xa - xsecant;
            this.getRootBracket().setLowerBound( secantRoot );
        }
        
        // Since fa and fb are assumed to have opposite sign, then
        // fb and fsecant must have the same sign, so throw away the
        // upper bound and replace it with the root of the secant.
        else
        {
            delta = xb - xsecant;
            this.getRootBracket().setUpperBound( secantRoot );
        }
        
        return Math.abs(delta) > this.getTolerance();
    }

    
}
