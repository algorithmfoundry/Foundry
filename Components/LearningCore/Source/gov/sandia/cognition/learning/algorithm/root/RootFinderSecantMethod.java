/*
 * File:                RootFinderSecantMethod.java
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
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * The secant algorithm for root finding.  This is a fast method but it is
 * known to fail even in real-world cases, when a function's slope tends to
 * zero the secant method will take near-infinite leaps.  This version of the
 * algorithm limits the maximum step size to ameliorate this problem.  The
 * algorithm works like a derivative-free approximation to the Newton-Raphson
 * root-finding method.  This is one of the fastest root-finding methods, but
 * may fail to find a root on real-world cases.  I would suggest using
 * Ridders's method.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Secant method",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Secant_method"
)
public class RootFinderSecantMethod 
    extends AbstractBracketedRootFinder
{
    
    /** 
     * Creates a new instance of RootFinderSecantMethod 
     */
    public RootFinderSecantMethod()
    {
        super();
    }

    private InputOutputSlopeTriplet previousPoint;

    @Override
    protected boolean initializeAlgorithm()
    {
        
        // Estimate the slope at the initial guess.
        double input = this.getInitialGuess();
        Evaluator<Double,Double> f = this.data;
        double forig = f.evaluate( input );
        final double delta = 1.0;
        double fdelta =  f.evaluate( input + delta );
        double slope = (fdelta-forig) / delta;
        
        this.previousPoint = new InputOutputSlopeTriplet( input, forig, slope ); 
        this.setRootBracket( new LineBracket( null, null, this.previousPoint ) );
        
        // If slope is flat, then secant method is hosed.
        return (slope != 0.0);
        
    }
  
    /**
     * Maximum step size allowed, {@value}
     */
    public static final double MAX_STEP = 1.0;
    
    @Override
    protected boolean step()
    {
//        System.out.println( this.getIteration() + ": Point: " + this.previousPoint );
        double xnm1 = this.previousPoint.getInput();
        double fnm1 = this.previousPoint.getOutput();
        double dnm1 = this.previousPoint.getSlope();

        double delta = fnm1 / dnm1;
        if( Math.abs(delta) > MAX_STEP )
        {
            delta = MAX_STEP * Math.signum( delta );
        }
        double xn = xnm1 - delta;
        double fn = this.data.evaluate( xn );
        double dn = (fn - fnm1) / (xn - xnm1);
        if( dn == 0.0 )
        {
            return false;
        }
        
        this.previousPoint = new InputOutputSlopeTriplet( xn, fn, dn );
        return (fn == 0.0) || (Math.abs(delta) >= this.getTolerance());

    }

    @Override
    public InputOutputPair<Double, Double> getResult()
    {
        return this.previousPoint;
    }
    
}
