/*
 * File:                LineBracketInterpolatorGoldenSection.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * Interpolates between the two bound points of a LineBracket using the
 * golden-section step rule, if that step fails, then the interpolator uses
 * a linear (secant) interpolation.  Neither of the bound points in the
 * LineBracket need slope information.
 * @author Kevin R. Dixon
 * @since 2.1
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Golden section search",
            type=PublicationType.WebPage,
            url="http://en.wikipedia.org/wiki/Golden_section_search",
            year=2008
        ),
        @PublicationReference(
            author={
                "Jeffrey Naisbitt",
                "Michael Heath"
            },
            title="Golden Section Search",
            type=PublicationType.WebPage,
            url="http://www.cse.uiuc.edu/iem/optimization/GoldenSection/",
            year=2008
        ),
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
            pages={401,402},
            url="http://www.nrbook.com/a/bookcpdf.php"
        )
    }
)
public class LineBracketInterpolatorGoldenSection 
    extends AbstractLineBracketInterpolator<Evaluator<Double,Double>>
{
    
    /**
     * Back-up interpolator using the secant method.
     */
    private LineBracketInterpolatorLinear linearInterpolator;
    
    /**
     * Golden ratio, from the Fibonacci sequence, {@value}
     */
    public final static double GOLDEN_RATIO = 1.618034;

    /**
     * The Golden Ratio conjugate, {@value}
     */
    public final static double GOLDEN_RATIO_CONJUGATE = GOLDEN_RATIO - 1.0;    
    
    /** 
     * Creates a new instance of LineBracketInterpolatorGoldenSection 
     */
    public LineBracketInterpolatorGoldenSection()
    {
        super( DEFAULT_TOLERANCE );
        this.linearInterpolator = new LineBracketInterpolatorLinear();
    }

    public double findMinimum(
        LineBracket bracket,
        double minx,
        double maxx,
        Evaluator<Double, Double> function )
    {
        
        double x = LineBracketInterpolatorGoldenSection.step(
            bracket.getLowerBound(), bracket.getUpperBound(), this.getTolerance() );

        // Make sure that "x" is between the required interval
        if( (minx <= x) && (x <= maxx) )
        {
            return x;
        }
        
        // Shoot... we're not inside the require interval,
        // so let's throw an exception.  Not sure if there's a better way to 
        // handle it, other than trying out each boundary and seeing which is
        // better.
        else
        {
            // I know... let's interpolate using a linear interpolator!
            return this.linearInterpolator.findMinimum( bracket, minx, maxx, function );
        }
        
    }
    
    /**
     * Takes a Golden Section step between the two points
     * @param a First point
     * @param b Second point
     * @param tolerance Minimum below which to consider the points identical.
     * @return
     * Interpolated Golden Section step between the two points
     */
    public static double step(
        InputOutputPair<Double,Double> a,
        InputOutputPair<Double,Double> b,
        double tolerance )
    {
        double x;
        double delta = b.getInput() - a.getInput();
        
        if( Math.abs( delta ) < tolerance )
        {
            throw new IllegalArgumentException(
                "Golden section delta has effectively collapsed: " + delta );
        }
        
        if (a.getOutput() < b.getOutput())
        {
            x = a.getInput() + GOLDEN_RATIO_CONJUGATE * delta;
        }
        else
        {
            x = b.getInput() - GOLDEN_RATIO_CONJUGATE * delta;
        }
        
        return x;
        
    }

    /**
     * Golden section step only needs the bounds to operate, so we'll just
     * check to make sure they're not null.
     * @param bracket
     * LineBracket to consider
     * @return
     * True if both bounds are not null, false otherwise
     */
    public boolean hasSufficientPoints(
        LineBracket bracket )
    {
        return (bracket.getLowerBound() != null) &&
            (bracket.getUpperBound() != null);
    }

}
