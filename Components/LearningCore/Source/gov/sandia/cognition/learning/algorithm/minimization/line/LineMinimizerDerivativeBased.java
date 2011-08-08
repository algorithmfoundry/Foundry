/*
 * File:                LineMinimizerDerivativeBased.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolator;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorHermiteParabola;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * This is an implementation of a line-minimization algorithm proposed by
 * Fletcher that makes extensive use of first-order derivative information.
 * The algorithm is provably correct and has good empirical performance.
 * <BR>
 * According to my test battery, this algorithm performs best using Hermite
 * parabolic interpolators (LineBracketInterpolatorHermiteParabola).
 * <BR>
 * My test battery LineMinimizerTestHarness minimizes over several different
 * functions including cosine and an absolute value of a cubic polynomial.
 * Here are the results in {function_evaluations, gradient_evaluation) pairs.
 * <BR>
 * LineBracketInterpolatorHermiteParabola:
 * cosine=(4.25,3.36), absolute_cubic=(8.09,5.02).
 * <BR>
 * LineBracketInterpolatorHermiteCubic:
 * cosine=(4.27,4.21), absolute_cubic=(7.52,6.55).
 * <BR>
 * LineBracketInterpolatorBrent:
 * cosine=(5.22,3.98), absolute_cubic=(9.44,6.27).
 * 
 * 
 * @author Kevin R. Dixon
 * @since 2.2
 */
@PublicationReference(
    author="R. Fletcher",
    title="Practical Methods of Optimization, Second Edition",
    type=PublicationType.Book,
    year=1987,
    pages={34, 39},
    notes={
        "Equation 2.6.2 and Equation 2.6.4",
        "Fletcher assumes that the initial slope is negative (WOLOG), and this class automatically adjusts itself to positive-slope guesses."
    }
)
public class LineMinimizerDerivativeBased 
    extends AbstractAnytimeLineMinimizer<DifferentiableUnivariateScalarFunction>
{

    /**
     * This is a fairly accurate line search, {@value}.
     */
    public final static double DEFAULT_CURVATURE_CONDITION = 0.1;

    /**
     * This is a fairly accurate line search, {@value}.
     */
    public final static double DEFAULT_SLOPE_CONDITION = DEFAULT_CURVATURE_CONDITION / 10.0;
    
    /**
     * Default interpolator to use to create a new candidate point to evaluate
     */
    public final static LineBracketInterpolator<? super DifferentiableUnivariateScalarFunction>
        DEFAULT_INTERPOLATOR = new LineBracketInterpolatorHermiteParabola();
 
    /**
     * Minimum value of the target function.  In other words, the user will
     * accept a solution less than or equal to minFunctionValue.  For many
     * applications 0.0 is a likely candidate (for cost functions, metrics,
     * least squares, etc.)
     */
    private double minFunctionValue;
    
    /**
     * Default minimum function value, {@value}.
     */
    public final static double DEFAULT_MIN_FUNCTION_VALUE = 0.0;
    
    /** 
     * Default constructor
     */
    public LineMinimizerDerivativeBased()
    {
        this( DEFAULT_MIN_FUNCTION_VALUE );
    }
    
    /**
     * Creates a new instance of LineMinimizerDerivativeBased 
     * @param minFunctionValue
     * Direction of the search.  Because Fletcher assumes the slope of the
     * initialGuess is less than 0.0, we have to flip around the direction
     * of search if the initial guess has positive slope.  Thus, direction=1.0
     * means that the initial slope was negative, while direction=-1.0 means
     * that the initial slope was positive.
     */
    public LineMinimizerDerivativeBased(
        double minFunctionValue )
    {
        this( ObjectUtil.cloneSafe( DEFAULT_INTERPOLATOR ), minFunctionValue );
    }

    /**
     * Creates a new instance of LineMinimizerDerivativeBased 
     * @param interpolator
     * Type of algorithm to fit data points and find an interpolated minimum
     * to the known points.
     * @param minFunctionValue
     * Direction of the search.  Because Fletcher assumes the slope of the
     * initialGuess is less than 0.0, we have to flip around the direction
     * of search if the initial guess has positive slope.  Thus, direction=1.0
     * means that the initial slope was negative, while direction=-1.0 means
     * that the initial slope was positive.
     */
    public LineMinimizerDerivativeBased(
        LineBracketInterpolator<? super DifferentiableUnivariateScalarFunction> interpolator,
        double minFunctionValue )
    {
        super( interpolator );
        this.setMinFunctionValue( minFunctionValue );
    }    
    
    /**
     * Direction of the search.  Because Fletcher assumes the slope of the
     * initialGuess is less than 0.0, we have to flip around the direction
     * of search if the initial guess has positive slope.  Thus, direction=1.0
     * means that the initial slope was negative, while direction=-1.0 means
     * that the initial slope was positive.
     */
    private double direction;
    
    /**
     * Internal function used to map/remap/unmap the search direction.
     */
    private InternalFunction internalFunction;    

    /**
     * The Wolfe conditions define approximate line search stopping criteria.
     */
    private WolfeConditions wolfe;

    /**
     * Maximum value of x in the search space.  That is, the minimizer will not
     * be greater than maxX.
     */
    private double maxX;

    /**
     * Suggested value given in PMOO=9.0, bottom of p.34, {@value}
     */
    protected static final double TAU1 = 5.0;

    /**
     * Suggested value given in PMOO=0.05, top of p.36 (given as 0.05 on p.69), {@value}
     */
    protected static final double TAU2 = 0.10;

    /**
     * Suggested value given in PMOO=0.50, top of p.36, {@value}
     */
    protected static final double TAU3 = 0.5;
    
    
    @Override
    protected boolean initializeAlgorithm()
    {
        boolean retval = super.initializeAlgorithm();

        // Set up the internal optimization function
        this.internalFunction = new InternalFunction();
        
        // I will store the points as the bounds
        this.setBracket( new LineBracket() );

        Double initialGuessFunctionValue;
        if( this.getInitialGuessFunctionValue() != null )
        {
            initialGuessFunctionValue = this.getInitialGuessFunctionValue();
        }
        else
        {
            initialGuessFunctionValue = this.data.evaluate( this.getInitialGuess() );
        }
        
        Double initialGuessSlope;
        if( this.getInitialGuessSlope() != null )
        {
            initialGuessSlope = this.getInitialGuessSlope();
        }
        else
        {
            initialGuessSlope = this.data.differentiate( this.getInitialGuess() );
        }
        
        // The initial point will be considered 0.0
        InputOutputSlopeTriplet initialTriplet = new InputOutputSlopeTriplet(
            this.internalFunction.convertInputToInternal( this.getInitialGuess() ), 
            initialGuessFunctionValue, initialGuessSlope );
        
        double initialSlope = initialTriplet.getSlope();
        
        // This is the "standard" downhill optimization, that is, increasing "x"
        // will initiall reduce the function
        if( initialSlope < 0.0 )
        {
            this.direction = 1.0;
        }
        
        // Fletcher assumes the initial slope is downhill, so reverse directions
        // if necessary
        else
        {
            this.direction = -1.0;
            initialTriplet.setSlope( initialSlope * this.direction );
        }
        
        this.getBracket().setLowerBound( initialTriplet );
                
        // Look for a nearly flat function and bound the search, because it's
        // likely to be hopeless
        if (Math.abs( initialSlope ) <= this.getTolerance()*1e-3)
        {
            // The initialTriplet hasn't yet been converted, so we can just return
            this.result = this.internalFunction.convertInputFromInternal( initialTriplet ); 
                
            this.stop();
            return true;
        }
        
        this.wolfe = new WolfeConditions( 
            initialTriplet, DEFAULT_SLOPE_CONDITION, DEFAULT_CURVATURE_CONDITION );
        
        double denom = this.wolfe.getSlopeCondition() * initialTriplet.getSlope();
        this.maxX = (this.getMinFunctionValue() - initialTriplet.getOutput()) / denom;
        
        // Here's the next point (alpha1)... the initial point becomes alpha0
        double nextX = 1.0;
        double fnextX = this.internalFunction.evaluate( nextX );
        this.getBracket().setUpperBound(
            new InputOutputSlopeTriplet( nextX, fnextX, null ) );
        
        return retval;
        
    }
    
    @Override
    public boolean bracketingStep()
    {
        
        LineBracket bracket = this.getBracket();
        
        // I'm storing the previous point (alpha_{i-1}) as the lower bound 
        // and the current point (alpha_i) as the upper bound.
        // This is useful for interpolation.
        
        // If we've already set the result, then we're done because we've
        // found a satifying point
        InputOutputSlopeTriplet previousPoint = bracket.getLowerBound();
        InputOutputSlopeTriplet currentPoint = bracket.getUpperBound();
        if( currentPoint.getOutput() < this.getMinFunctionValue() )
        {
            this.result =
                this.internalFunction.convertInputFromInternal( currentPoint );
            return true;
        }
        
        if (!this.wolfe.evaluateGoldsteinCondition( currentPoint ) ||
            currentPoint.getOutput() >= previousPoint.getOutput())
        {
            // We've found a valid bracket!  So we're done bracketing!
            bracket.setLowerBound( previousPoint );
            bracket.setUpperBound( currentPoint );
            return true;
        }
        
        // Compute the slope of the current point,
        // if it hasn't already been computed
        if( currentPoint.getSlope() == null )
        {
            currentPoint.setSlope( 
                this.internalFunction.differentiate( currentPoint.getInput() ) );
        }

        // If we meet the Wolfe conditions, then we're done already!!
        if (this.wolfe.evaluateStrictCurvatureCondition( currentPoint.getSlope() ))
        {
            this.result =
                this.internalFunction.convertInputFromInternal( currentPoint );
            return true;
        }

        // We've found a point whose slope is increasing.
        // Since the original point is assumed (forced) to have negative slope,
        // this implies that somewhere between the original point and the
        // current point, there exists a minimum.
        // Furthermore, by induction, we can infer that "previousPoint" also
        // had negative slope as well.  This means that there exists a
        // minimum somewhere between previous point and current point.
        if( currentPoint.getSlope() >= 0.0 )
        {
            bracket.setLowerBound( currentPoint );
            bracket.setUpperBound( previousPoint );
            return true;
        }

        // We haven't bracketed a minimum, so let's find a promising point
        double delta = currentPoint.getInput() - previousPoint.getInput();
        double deltaPlusCurrent = currentPoint.getInput() + delta;
        double nextX;
        if (this.maxX <= deltaPlusCurrent)
        {
            nextX = this.maxX;
        }
        else
        {
            double minx = deltaPlusCurrent;
            double maxx = Math.min( this.maxX, currentPoint.getInput() + TAU1 * delta );
            if( minx > maxx )
            {
                double temp = minx;
                minx = maxx;
                maxx = temp;
            }
            
            // Let's interpolate between [minx,maxx] using the points we've
            // got available
            nextX = this.getInterpolator().findMinimum(
                bracket, minx, maxx, this.internalFunction );
        }
        
        // We haven't found an appropriate bracket yet, so keep on trucking
        bracket.setOtherPoint( previousPoint );
        bracket.setLowerBound( currentPoint );
        bracket.setUpperBound( new InputOutputSlopeTriplet(
            nextX, this.internalFunction.evaluate( nextX ), null ) );
        
        return false;
    }

    @Override
    public boolean sectioningStep()
    {
        
        LineBracket bracket = this.getBracket();
        InputOutputSlopeTriplet a = bracket.getLowerBound();
        InputOutputSlopeTriplet b = bracket.getUpperBound();
        
        // See if the bracket has converged... if so, then stop
        double bracketDelta = b.getInput() - a.getInput();
        if( Math.abs(bracketDelta) < this.getTolerance() )
        {
            this.result = this.internalFunction.convertInputFromInternal(
                (a.getOutput() < b.getOutput()) ? a : b );
            return false;
        }

        double minx = a.getInput() + TAU2 * bracketDelta;
        double maxx = b.getInput() - TAU3 * bracketDelta;
        if( minx > maxx )
        {
            double temp = minx;
            minx = maxx;
            maxx = temp;
        }

        // Let's interpolate between [minx,maxx] using a Hermite polynomial
        double alphaj = this.getInterpolator().findMinimum(
            bracket, minx, maxx, this.internalFunction );
        double falphaj = this.internalFunction.evaluate( alphaj );
        
        InputOutputSlopeTriplet currentPoint =
            new InputOutputSlopeTriplet( alphaj, falphaj, null );

        // Let's check for convergence on the bracket
        double midx = 0.5 * (minx + maxx);
        double convergenceThreshold =
            this.getTolerance()*Math.abs(b.getInput()) - 0.5*(maxx-minx);

        // This checks for converence along the x-axis and "flatness" on the
        // y-axis
        if( (Math.abs(midx-alphaj) <= convergenceThreshold) ||
            (falphaj < this.getMinFunctionValue()) )
        {
            this.result = this.internalFunction.convertInputFromInternal(
                currentPoint );
            return false;
        }
        
        // Use the interpolated point to update the high-side bound
        if (!this.wolfe.evaluateGoldsteinCondition( currentPoint ) ||
            falphaj >= a.getOutput())
        {
            bracket.setOtherPoint( b );
            b = currentPoint;
        }
        else
        {
            if( currentPoint.getSlope() == null )
            {
                currentPoint.setSlope(
                    this.internalFunction.differentiate( alphaj ) );
            }

            // We've met the Wolfe conditions, so we're done!
            if (this.wolfe.evaluateStrictCurvatureCondition(
                currentPoint.getSlope() ))
            {
                this.result = this.internalFunction.convertInputFromInternal( 
                    currentPoint );
                return false;
            }

            // Use the interpolated point to update the low-side bound
            InputOutputSlopeTriplet previousA = a;
            bracket.setOtherPoint( previousA );
            a = currentPoint;

            // See if we should update the high-side bound
            // using the low-side if the slope has changed directions
            if (bracketDelta * currentPoint.getSlope() >= 0.0)
            {
                bracket.setOtherPoint( b );
                b = previousA;
            }

        }
        
        bracket.setLowerBound( a );
        bracket.setUpperBound( b );
    
        return true;
        
    }

    /**
     * Getter for minFunctionValue
     * @return
     * Minimum value of the target function.  In other words, the user will
     * accept a solution less than or equal to minFunctionValue.
     */
    public double getMinFunctionValue()
    {
        return this.minFunctionValue;
    }

    /**
     * Setter for minFunctionValue
     * @param minFunctionValue
     * Minimum value of the target function.  In other words, the user will
     * accept a solution less than or equal to minFunctionValue.
     */
    public void setMinFunctionValue(
        double minFunctionValue )
    {
        this.minFunctionValue = minFunctionValue;
    }
    
    /**
     * Internal function used to map/remap/unmap the search direction.
     */
    public class InternalFunction
        extends AbstractDifferentiableUnivariateScalarFunction
    {

        /**
         * Converts a real-world "x" value to the internal values used inside
         * the search algorithm.  This compensates for reflecting the search
         * space
         * @param input
         * Input value in the real-world
         * @return
         * X-axis value to send to the InternalFunction
         */
        public double convertInputToInternal(
            double input )
        {
            double x0 = LineMinimizerDerivativeBased.this.getInitialGuess();
            double internalInput = LineMinimizerDerivativeBased.this.direction * (input-x0);            
            return internalInput;
        }
        
        /**
         * Converts the internal x-axis value to real-world x-axis value
         * @param internalInput
         * internalInput to convert
         * @return
         * real-world x-axis value
         */
        protected double convertInputFromInternal(
            double internalInput )
        {
            double x0 = LineMinimizerDerivativeBased.this.getInitialGuess();
            double input = x0 + LineMinimizerDerivativeBased.this.direction*internalInput;
            return input;            
        }
        
        /**
         * Converts an InternalFunction InputOutputSlopeTriplet to a real-world
         * InputOutputSlopeTriplet by unreflection and flipping the sign of the
         * slope (if the direction of search was backward).
         * @param internalPoint
         * InternalFunction-based point to manipulate
         * @return
         * Real-world value
         */
        public InputOutputSlopeTriplet convertInputFromInternal(
            InputOutputSlopeTriplet internalPoint )
        {
            InputOutputSlopeTriplet retval;
            
            double input = this.convertInputFromInternal( 
                internalPoint.getInput() );
            if( LineMinimizerDerivativeBased.this.direction > 0.0 )
            {
                retval = new InputOutputSlopeTriplet(
                    input, internalPoint.getOutput(), internalPoint.getSlope() );
            }
            else
            {
                Double m = (internalPoint.getSlope() != null) ? -internalPoint.getSlope() : null;
                retval = new InputOutputSlopeTriplet(
                    input, internalPoint.getOutput(), m );
            }
            
            return retval;
            
        }
        
        public double evaluate(
            double internalInput )
        {
            return LineMinimizerDerivativeBased.this.data.evaluate(
                this.convertInputFromInternal( internalInput ) );
        }

        public double differentiate(
            double internalInput )
        {
            return LineMinimizerDerivativeBased.this.direction * 
                LineMinimizerDerivativeBased.this.data.differentiate(
                  this.convertInputFromInternal( internalInput ) );
        }
        
    }    

}
