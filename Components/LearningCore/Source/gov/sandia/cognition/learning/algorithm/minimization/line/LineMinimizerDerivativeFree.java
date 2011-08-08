/*
 * File:                LineMinimizerDerivativeFree.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolator;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorBrent;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorGoldenSection;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This is an implementation of a LineMinimizer that does not require 
 * derivative information.  This implementation is much slower than its cousin
 * that uses derivative information, LineMinimizerDerivativeBased.  In
 * particular, the bracketing phase of this class is much slower than its
 * cousin.  However, this implementation is appears to be faster than using
 * finite-differences to approximate derivative information in conjunction with 
 * the derivative-based line minimizer.
 * <BR>
 * <BR>
 * My recommendation: use LineMinimizerDerivativeBased whenever derivatives are
 * available, but LineMinimizerDerivativeFree otherwise (even when
 * approximating derivatives).
 * <BR>
 * This implementation is loosely based on the Numerical Recipes function
 * "Brent method," however I've corrected for some serious inefficiencies in
 * that code.
 *
 * @author Kevin R. Dixon
 * @since 2.2
 */
@PublicationReference(
    author={
        "William H. Press",
        "Saul A. Teukolsky",
        "William T. Vetterling",
        "Brian P. Flannery"
    },
    title="Numerical Recipes in C, Second Edition",
    type=PublicationType.Book,
    year=1992,
    pages={400,405},
    url="http://www.nrbook.com/a/bookcpdf.php"
)
public class LineMinimizerDerivativeFree
    extends AbstractAnytimeLineMinimizer<Evaluator<Double,Double>>
{

    /**
     * Maximum step size allowed by a parabolic fit, {@value}.
     */
    public static final double STEP_MAX = 100.0;
    
    /**
     * Default interpolation algorithm, LineBracketInterpolatorBrent.
     */
    public static final LineBracketInterpolator<? super Evaluator<Double,Double>> DEFAULT_INTERPOLATOR =
        new LineBracketInterpolatorBrent();

    /** 
     * Creates a new instance of LineMinimizerDerivativeFree
     */
    public LineMinimizerDerivativeFree()
    {
        this( ObjectUtil.cloneSafe( DEFAULT_INTERPOLATOR ) );
    }
    
    /**
     * Creates a new instance of LineMinimizerDerivativeFree
     * 
     * @param interpolator
     * Type of algorithm to fit data points and find an interpolated minimum
     * to the known points.
     */
    public LineMinimizerDerivativeFree(
        LineBracketInterpolator<? super Evaluator<Double,Double>> interpolator )
    {
        super( interpolator );
    }

    /**
     * Here's the general idea of derivative-free minimum bracketing:
     * <BR><BR>
     * Given an initial point, a={x,f(x)}, we're looking to find a triplet
     * of points {a,b,c} such that bx is between ax and cx.
     * Furthermore, we need f(bx) less than both f(ax) and f(cy).  This
     * necessarily implies that there exists a minimum between ax and cx.
     * To find these mythical points, the first step here is to fit a parabola
     * to the existing points to determine where there should be a local
     * minimum. A few things can happen due to this parabolic fit. The
     * hypothesized minimum of the parabola:
     *  - provides a proper brack to a minimum on [b,newpoint,c] or
     * [a,b,newpoint]
     * - is outside the current set [a,b,c,newpoint], so we'll look for a
     * minimum between c and newpoint using golden-section step
     * - is exhibiting signs of numerical instability.  We'll throw out the
     * minimum, set it to a maximum step and use golden-section between c and
     * maxstep.
     * <BR><BR>
     * However, it's possible/likely that the parabola doesn't provide any
     * information about where a minimum is.  If this is the case, then we'll
     * just take a golden-section step between b and c.  If that doesn't expose
     * a minimum, then we'll replace the oldest point, a, with newpoint and
     * then we'll fit another parabola next iteration to the new points.
     * <BR><BR>
     * When the method returns true, then we have a bracket with a minimum
     * between ax and cx, furthermore:
     * ax<bx<cx and f(bx)<f(ax) and f(bx)<f(cx)
     * 
     * @return
     * true means we have a valid bracket, false means we do not (yet) have
     * a valid bracket
     */
    @PublicationReference(
        author={
            "William H. Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes in C, Second Edition",
        type=PublicationType.Book,
        year=1992,
        pages={400,401},
        url="http://www.nrbook.com/a/bookcpdf.php"
    )
    @Override
    public boolean bracketingStep()
    {
        
        boolean validBracket;
        
        // Changing the name from "data" to "f" to avoid confusion
        // And caching some other values
        Evaluator<Double,Double> f = this.data;
        LineBracket bracket = this.getBracket();
        
        // We'll use the initialGuess as the hard lower limit
        if( bracket.getLowerBound() == null )
        {
            double x = this.getInitialGuess();
            double fx = (this.getInitialGuessFunctionValue() != null)
                ? this.getInitialGuessFunctionValue() : f.evaluate( x );
            bracket.setLowerBound( new InputOutputSlopeTriplet( x, fx, null ) );
        }
        
        // Set the next point to be an arbitrary distance from the
        // starting point, such as 1.0
        if( bracket.getOtherPoint() == null )
        {
            
            double step = 1.0;
            if( (this.getInitialGuessSlope() != null) &&
                (this.getInitialGuessSlope() > 0.0) )
            {
                step = -1.0;
            }
            
            double x = bracket.getLowerBound().getInput() - step;
            double fx = f.evaluate( x );
            InputOutputSlopeTriplet b = new InputOutputSlopeTriplet( x, fx, null );
            
            // From these two points, ensure lowerBound.getOutput >= b.getOutput
            if( bracket.getLowerBound().getOutput() < b.getOutput() )
            {
                bracket.setOtherPoint( bracket.getLowerBound() );
                bracket.setLowerBound( b );
            }
            else
            {
                bracket.setOtherPoint( b );
            }
            
        }
        
        // If the upper bound isn't defined, then just take a golden section
        // step beyond the middle point
        if( bracket.getUpperBound() == null )
        {
            double ax = bracket.getLowerBound().getInput();
            double bx = bracket.getOtherPoint().getInput();
            double cx = 
                bx + LineBracketInterpolatorGoldenSection.GOLDEN_RATIO * (bx-ax);
            double fcx = f.evaluate( cx );
            bracket.setUpperBound( new InputOutputSlopeTriplet( cx, fcx, null ) );
        }
        
        InputOutputSlopeTriplet a = bracket.getLowerBound();
        InputOutputSlopeTriplet b = bracket.getOtherPoint();
        InputOutputSlopeTriplet c = bracket.getUpperBound();
        
        if( a.getOutput() < b.getOutput() )
        {
            throw new IllegalArgumentException(
                "Discovered a.getOutput < b.getOutput!  This should never happen during bracketing!" );
        }
        
        // By induction, we already know that a.getOutput >= b.getOutput
        if( b.getOutput() > c.getOutput() )
        {
            // Make sure that minx < maxx
            double minx = b.getInput();
            double maxx = minx + STEP_MAX * (c.getInput() - b.getInput());
            if( minx > maxx )
            {
                double temp = minx;
                minx = maxx;
                maxx = temp;
            }
            
            // The interpolator says that "xstar" is the minimum interpolated 
            // point on the [minx,maxx] interval.  This interval is anchored
            // on one side at bx and is a superset of the interval [bx,cx].
            // Therefore, the only possible outcomes are that "xstar" is on the
            // interval [bx,cx] or (cx,maxx].
            double xstar = 
                this.getInterpolator().findMinimum( bracket, minx, maxx, f );
            double fxstar = f.evaluate( xstar );
            InputOutputSlopeTriplet star =
                new InputOutputSlopeTriplet( xstar, fxstar, null );
            
            this.result = star;
            
            // The interpolated minimum is on the (bx,cx) interval, so let's
            // see if we can prove there's a minimum in there
            // (Note: this funky truth table is because ax<bx<cx OR ax>bx>cx
            // and I'm not keeping the point sorted in strictly increasing
            // or decreasing order.)
            if( (b.getInput() - xstar) * (xstar - c.getInput()) > 0.0 )
            {
                // We already know that b.getOutput > c.getOutput and
                // fxstar < c.getOutput, then we have that:
                // fxstar < b.getOutput AND fxstar < c.getOutput AND
                // since xstar is between bx and cx, then there must be a
                // minimum between bx and cx
                if( fxstar < c.getOutput() )
                {
                    bracket.setLowerBound( b );
                    bracket.setOtherPoint( star );
                    // It's already the case that bracket.getUpperBound()==c
                    validBracket = true;
                }
                
                // We already know that a.getOutput > b.getOutput by induction
                // and b.getOutput < fxstar, then we have that:
                // b.getOutput < a.getOutput AND b.getOutput < fxstar AND
                // since bx is between ax and fxstar, then there must be a
                // minimum between ax and bx
                else if( b.getOutput() < fxstar )
                {
                    // It's already the case that bracket.getLowerBound()==a
                    // It's already the case that bracket.getPoints.getLast()==b
                    bracket.setUpperBound( star );
                    validBracket = true;
                }
                else
                {
                    // The parabola didn't reveal a minimum...
                    bracket.setOtherPoint( star );
                    validBracket = false;
                }
                
            }
            
            // The interpolated point is beyond "c", but we can still check to
            // see if we've bracket a minimum with this point
            else
            {
                // Get rid of the point "a", since we know that the
                // interpolated point is beyond point "c"
                bracket.setLowerBound( b );
                bracket.setOtherPoint( c );
                bracket.setUpperBound( 
                    new InputOutputSlopeTriplet( xstar, fxstar, null ) );
                
                // We already know that b.getOutput > c.getOutput and
                // and c.getOutput < fxstar, then we have that:
                // c.getOutput < b.getOutput AND c.getOutput < fxstar AND
                // cx is between bx and xstar, then there must be a minimum
                // between bx and xstar
                if( c.getOutput() < fxstar )
                {
                    validBracket = true;
                }
                else
                {
                    validBracket = false;
                }
            }
            
        }
        
        // We've already discovered a valid bracket, so just call it quits
        else
        {
            validBracket = true;
        }
        
        // If we've got a valid bracket, then ensure that our bracket is
        // properly sorted, lowerBound.getInput < upperBound.getInput
        if( validBracket )
        {
            if( bracket.getLowerBound().getInput() > bracket.getUpperBound().getInput() )
            {
                InputOutputSlopeTriplet temp = bracket.getLowerBound();
                bracket.setLowerBound( bracket.getUpperBound() );
                bracket.setUpperBound( temp );
            }
        }
                
        return validBracket;
        
    }
    
    @PublicationReference(
        author={
            "William H. Press",
            "Saul A. Teukolsky",
            "William T. Vetterling",
            "Brian P. Flannery"
        },
        title="Numerical Recipes in C, Second Edition",
        type=PublicationType.Book,
        year=1992,
        pages={404,405},
        url="http://www.nrbook.com/a/bookcpdf.php"
    )
    @Override
    public boolean sectioningStep()
    {
        // I already have a legitimate bracket set up, so we just need to
        // interpolate our way to glory!!
        LineBracket bracket = this.getBracket();
        InputOutputSlopeTriplet a = bracket.getLowerBound();
        InputOutputSlopeTriplet b = bracket.getOtherPoint();
        InputOutputSlopeTriplet c = bracket.getUpperBound();
        
        // We already know that ax < bx < cx
        // AND f(bx) <= f(ax) AND f(bx) <= f(cx)
        
        // Let's see if the points have become "flat"... if so, then there's
        // no reason to believe there's a better minimum, so we should stop
        double dab = a.getOutput() - b.getOutput();
        double dbc = b.getOutput() - c.getOutput();
        
        // Set up the required interval to be slightly smaller than it is
        // currently.  This ensures convergence.
        double minx = a.getInput() + this.getTolerance()*Math.signum(a.getInput());
        double maxx = c.getInput() - this.getTolerance()*Math.signum(c.getInput());
        
        // Let's check for convergence on the bracket
        double midx = 0.5 * (minx + maxx);
        double convergenceThreshold =
            this.getTolerance()*Math.abs(b.getInput()) - 0.5*(maxx-minx);

        // This checks for converence along the x-axis and "flatness" on the
        // y-axis
        if( (Math.abs(midx-b.getInput()) <= convergenceThreshold) ||
            (Math.max( Math.abs(dab), Math.abs(dbc) ) < this.getTolerance()) )
        {
            this.result = b;
            return false;
        }

        // Change the name of "this.data" to "f" to avoid confusion
        Evaluator<Double,Double> f = this.data;

        // Find the next minimum interpolated point.  We know that xstar
        // will be on the interval (ax,cx)
        double xstar = this.getInterpolator().findMinimum(
            bracket, minx, maxx, f );
        double fxstar = f.evaluate( xstar );
        InputOutputSlopeTriplet star =
            new InputOutputSlopeTriplet( xstar, fxstar, null );
        
        // If the interpolated point was better than the last point we tried,
        // then let's replace one of the bracket bounds with the previous point
        if( fxstar < b.getOutput() )
        {
            // The interpolated point is in (ax,bx], so let's set the bracket
            // as [a,b], because ax < xstar < bx AND
            // f(xstar) < f(bx) AND f(xstar) <= f(cx)
            if( xstar <= b.getInput() )
            {
                // a == a already
                c = b;
                b = star;
            }
            
            // The interpolated point is in (bx,cx), so let's set the bracket
            // as [b,c], because bx < xstar < cx AND
            // f(xstar) < f(bx) AND f(xstar) <= f(cx)
            else
            {
                a = b;
                b = star;
                // c == c already
            }
            
        }
        
        // The interpolated point was worse than the previous point, so let's
        // use the interpolated point to replace one of the bounds
        else
        {
            
            // The interpolated point is in (ax,bx], so let's set the bracket
            // as [xstar,cx], because xstar < bx < cx AND 
            // f(bx) <= f(xstar) AND f(bx) <= f(cx)
            if( xstar <= b.getInput() )
            {
                a = star;
                // b == b already
                // c == c already
            }
            
            // The interpolated point is in (bx,cx), so let's set the bracket
            // as [ax,xstar], because ax < bx < xstar AND
            // f(bx) <= f(ax) AND f(bx) <= f(xstar)
            else
            {
                // a == a already
                // b == b already
                c = star;
            }
            
        }
        
        // Update the bracket using the points {a,b,c}
        this.result = b;
        bracket.setLowerBound( a );
        bracket.setOtherPoint( b );
        bracket.setUpperBound( c );
        
        // We're not converged, so keep on squeezing that bracket!
        return true;
        
    }

}
