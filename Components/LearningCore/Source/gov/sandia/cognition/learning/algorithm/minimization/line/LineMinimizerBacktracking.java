/*
 * File:                LineMinimizerBacktracking.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 1, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;

/**
 * Implementation of the backtracking line-minimization algorithm.  This
 * algorithm takes a full Newton step in the direction of a minimum and,
 * if this does not find a sufficiently decreasing function evaluation, it
 * then successively reduces the step length and tries again until a
 * sufficiently decreasing value is found.
 * <BR>
 * This is a very inexact line minimizer, but it appears to help the
 * performance of both Quasi-Newton and Conjugate Gradient algorithms.
 * In my tests, it appears to reduce the computation by about 30%-60%.
 * 
 * @author Kevin R. Dixon
 * @since 2.1
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
    pages={384,386},
    url="http://www.nrbook.com/a/bookcpdf.php"
)
public class LineMinimizerBacktracking 
    extends AbstractAnytimeLineMinimizer<Evaluator<Double,Double>>
{

    /**
     * Maximum step size allowed by a parabolic fit, {@value}
     */
    public static final double STEP_MAX = 100.0;

    /**
     * Default sufficient decrease value, {@value}
     */
    public static final double DEFAULT_SUFFICIENT_DECREASE = 0.5;
    
    /**
     * Default amount to decrease the step amount each iteration, {@value}.
     */
    public static final double DEFAULT_GEOMETRIC_DECREASE = 0.5;

    /**
     * Default flag to use numerical differentiation, {@value}.
     */
    public static final boolean DEFAULT_NUMERICAL_DERIVATIVE = true;

    /**
     * Sufficient decrease condition, must be (0,1).  Smaller values (0.1)
     * result in more accurate searches, larger values (0.9) tend to be 
     * easier to satisfy.
     */
    private double sufficientDecrease;

    /**
     * Amount to decrease the step amount each iteration.
     */
    private double geometricDecrease;
    
    /**
     * Flag whether or not to use the numerical differentiation.
     */
    private boolean numericalDerivative;

    /** 
     * Creates a new instance of LineMinimizerBacktracking 
     */
    public LineMinimizerBacktracking()
    {
        this( DEFAULT_GEOMETRIC_DECREASE );
    }
    
    /**
     * Creates a new instance of LineMinimizerBacktracking 
     * @param geometricDecrease
     * Amount to decrease the step amount each iteration.
     */
    public LineMinimizerBacktracking(
        double geometricDecrease )
    {
        this( geometricDecrease, DEFAULT_NUMERICAL_DERIVATIVE );
    }

    /**
     * Creates a new instance of LineMinimizerBacktracking
     * @param geometricDecrease
     * Amount to decrease the step amount each iteration.
     * @param numericalDerivative
     * Flag whether or not to use the numerical differentiation.
     */
    public LineMinimizerBacktracking(
        double geometricDecrease,
        boolean numericalDerivative )
    {
        this( geometricDecrease, numericalDerivative, DEFAULT_SUFFICIENT_DECREASE );
    }

    /**
     * Creates a new instance of LineMinimizerBacktracking 
     * @param geometricDecrease
     * Amount to decrease the step amount each iteration.
     * @param numericalDerivative
     * Flag whether or not to use the numerical differentiation.
     * @param sufficientDecrease
     * Sufficient decrease condition, must be (0,1).  Smaller values (0.1)
     * result in more accurate searches, larger values (0.9) tend to be 
     * easier to satisfy.
     */
    public LineMinimizerBacktracking(
        double geometricDecrease,
        boolean numericalDerivative,
        double sufficientDecrease )
    {
        super( null );
        this.setGeometricDecrease(geometricDecrease);
        this.setNumericalDerivative(numericalDerivative);
        this.setSufficientDecrease( sufficientDecrease );
    }
    
    /**
     * Current value of the step size.
     */
    private double stepValue;

    @Override
    protected boolean initializeAlgorithm()
    {
        // Ignore the return from the super method, as all that matters is if
        // we have an initial guess.
        super.initializeAlgorithm();
        return (this.getInitialGuess() != null);
    }    
    
    @Override
    public boolean bracketingStep()
    {
        
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
        else if( !this.getNumericalDerivative() &&
            (this.data instanceof DifferentiableUnivariateScalarFunction) )
        {
            initialGuessSlope =
                ((DifferentiableUnivariateScalarFunction) this.data).differentiate( this.getInitialGuess() );
        }
        else
        {
            initialGuessSlope = NumericalDifferentiator.DoubleJacobian.differentiate(
                this.getInitialGuess(), this.data );
        }
        
        // The initial point will be considered 0.0
        InputOutputSlopeTriplet initialTriplet = new InputOutputSlopeTriplet(
            this.getInitialGuess(),
            initialGuessFunctionValue,
            initialGuessSlope );
        
        double slope = initialTriplet.getSlope();
        
        // The initialTriplet hasn't yet been converted, so we can just return
        this.result = initialTriplet;
        
        // Look for a nearly flat function and bound the search, because it's
        // likely to be hopeless
        if (Math.abs(slope) <= this.getTolerance())
        {
            this.stop();
            return true;
        }

        // Compute the Newton step.  If that's too far
        double newtonStep = -Math.abs(initialGuessFunctionValue) / slope;
        if( Math.abs(newtonStep) > STEP_MAX )
        {
            newtonStep = -Math.signum(slope) * STEP_MAX;
        }
        this.stepValue = newtonStep;

        // We only know the lower bound right now
        this.getBracket().setLowerBound( initialTriplet );
        return true;
    }

    @Override
    public boolean sectioningStep()
    {

        LineBracket bracket = this.getBracket();
        InputOutputSlopeTriplet initialTriplet = bracket.getLowerBound();

        double x = this.stepValue + initialTriplet.getInput();
        double fx = this.data.evaluate( x );
        InputOutputSlopeTriplet trialPoint = 
            new InputOutputSlopeTriplet( x, fx, null );
        
        bracket.setOtherPoint( bracket.getUpperBound() );
        bracket.setUpperBound( trialPoint );

        // Store this point off
        if( this.result.getOutput() > trialPoint.getOutput() )
        {
            this.result = trialPoint;
        }

        if( WolfeConditions.evaluateGoldsteinCondition(
            initialTriplet, trialPoint, this.getSufficientDecrease() ) )
        {
            this.result = trialPoint;
            return false;
        }

        this.stepValue *= this.getGeometricDecrease();

        return (Math.abs(this.stepValue) > this.getTolerance());
        
    }

    /**
     * Getter for sufficientDecrease
     * @return
     * Sufficient decrease condition, must be (0,1).  Smaller values (0.1)
     * result in more accurate searches, larger values (0.9) tend to be 
     * easier to satisfy.
     */
    public double getSufficientDecrease()
    {
        return this.sufficientDecrease;
    }

    /**
     * Setter for sufficientDecrease
     * @param sufficientDecrease
     * Sufficient decrease condition, must be (0,1).  Smaller values (0.1)
     * result in more accurate searches, larger values (0.9) tend to be 
     * easier to satisfy.
     */
    public void setSufficientDecrease(
        double sufficientDecrease )
    {
        if( (sufficientDecrease <= 0.0) ||
            (sufficientDecrease >= 1.0) )
        {
            throw new IllegalArgumentException(
                "sufficientDecrease must be (0,1)" );
        }
        this.sufficientDecrease = sufficientDecrease;
    }

    /**
     * Getter for geometricDecrease.
     * @return 
     * Amount to decrease the step amount each iteration.
     */
    public double getGeometricDecrease()
    {
        return this.geometricDecrease;
    }

    /**
     * Setter for geometricDecrease
     * @param geometricDecrease
     * Amount to decrease the step amount each iteration.
     */
    public void setGeometricDecrease(
        double geometricDecrease)
    {
        if( (geometricDecrease <= 0.0) ||
            (geometricDecrease >= 1.0) )
        {
            throw new IllegalArgumentException( "geometricDecrease must be (0,1)" );
        }
        this.geometricDecrease = geometricDecrease;
    }

    /**
     * Getter for numericalDerivative
     * @return 
     * Flag whether or not to use the numerical differentiation.
     */
    public boolean getNumericalDerivative()
    {
        return this.numericalDerivative;
    }

    /**
     * Setter for numericalDerivative
     * @param numericalDerivative
     * Flag whether or not to use the numerical differentiation.
     */
    public void setNumericalDerivative(
        boolean numericalDerivative)
    {
        this.numericalDerivative = numericalDerivative;
    }

}
