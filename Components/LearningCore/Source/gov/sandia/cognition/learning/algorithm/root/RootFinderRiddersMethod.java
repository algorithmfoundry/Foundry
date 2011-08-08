/*
 * File:                RootFinderRiddersMethod.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 6, 2009, Sandia Corporation.
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
 * The root-finding algorithm due to Ridders.  This algorithm is guaranteed
 * to find a root if given a valid bracket.  It tends to have very good
 * performance on real-world cases.  It works by performing stable logarithmic
 * interpolation between function evaluations in a way that maintains a
 * rigorous bracket on a root.  It has good overall performance and will
 * converge to a root.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Ridders' Method",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Ridders%27_method"
)
public class RootFinderRiddersMethod 
    extends AbstractBracketedRootFinder
{

    /** 
     * Creates a new instance of RootFinderRiddersMethod 
     */
    public RootFinderRiddersMethod()
    {
    }

    @Override
    protected boolean step()
    {
        
        double x1 = this.getRootBracket().getLowerBound().getInput();
        double f1 = this.getRootBracket().getLowerBound().getOutput();
        double x2 = this.getRootBracket().getUpperBound().getInput();
        double f2 = this.getRootBracket().getUpperBound().getOutput();
        
        double xmid = 0.5 * (x1+x2);
        double fmid = this.data.evaluate( xmid );
        
        InputOutputSlopeTriplet midpoint =
            new InputOutputSlopeTriplet( xmid, fmid, null );
        
        double t1 = Math.sqrt( fmid*fmid - f1*f2 );
        if( t1 == 0.0 )
        {
            return false;
        }
        
        double xnew = xmid + (xmid-x1)*Math.signum(f1-f2)*fmid / t1;
        double fnew = this.data.evaluate( xnew );
        InputOutputSlopeTriplet newpoint =
            new InputOutputSlopeTriplet( xnew, fnew, null );
        
        this.getRootBracket().setOtherPoint( newpoint );
         
        if( fnew == 0.0 )
        {
            return false;
        }
        
        // If the midpoint and the new point have opposite signs, then we've
        // found a much tighter bracket!!
        if( fmid * fnew < 0.0 )
        {
            this.getRootBracket().setLowerBound( midpoint );
            this.getRootBracket().setUpperBound( newpoint );
        }
        
        // If the new point has the same sign as the lower bound, then we
        // can throw away the old lower bound
        else if( f1*fnew > 0.0 )
        {
            this.getRootBracket().setLowerBound( newpoint );
        }
        
        // If the new point has the same sign as the upper bound, then
        // we can throw away the old upper bound.
        else if( f2*fnew > 0.0 )
        {
            this.getRootBracket().setUpperBound( newpoint );
        }
        
        else
        {
            throw new IllegalArgumentException( "You should never get here: "
                + "f1: " + f1 + ", f2: " + f2 + ", fnew: " + fnew + ", fmid: " + fmid );
        }
        
        double delta = this.getRootBracket().getLowerBound().getInput()
            - this.getRootBracket().getUpperBound().getInput();
        return Math.abs(delta) > this.getTolerance();
        
    }

    
}
