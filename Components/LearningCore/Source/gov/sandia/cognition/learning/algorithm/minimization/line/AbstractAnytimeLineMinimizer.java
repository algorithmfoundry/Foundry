/*
 * File:                AbstractAnytimeLineMinimizer.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.minimization.AbstractAnytimeFunctionMinimizer;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolator;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Partial AnytimeAlgorithm implementation of a LineMinimizer.
 * @param <EvaluatorType> Type of Evaluator to use
 * @author Kevin R. Dixon
 * @since 2.1
 */
public abstract class AbstractAnytimeLineMinimizer<EvaluatorType extends Evaluator<Double,Double>>
    extends AbstractAnytimeFunctionMinimizer<Double,Double,EvaluatorType>
    implements LineMinimizer<EvaluatorType>
{    
    
    /**
     * Default number of iterations to run the algorithm, {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100;
    
    /**
     * Default tolerance of the algorithm {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-5;    

    /**
     * LineBracket bounding a local minimum.
     */
    private LineBracket bracket;
    
    /**
     * Flag indicating if the algorithm has already found a valid bracket on
     * a local minimum.
     */
    private boolean validBracket;
    
    /**
     * Type of algorithm to fit data points and find an interpolated minimum
     * to the known points.
     */
    private LineBracketInterpolator<? super EvaluatorType> interpolator;

    /**
     * Function value at the initialGuess, may be null.
     */
    private Double initialGuessFunctionValue;
    
    /**
     * Function slope at the initialGuess, may be null.
     */
    private Double initialGuessSlope;
    
    /** 
     * Creates a new instance of AbstractAnytimeLineMinimizer 
     * @param interpolator 
     * Type of algorithm to fit data points and find an interpolated minimum
     * to the known points.
     */
    public AbstractAnytimeLineMinimizer(
        LineBracketInterpolator<? super EvaluatorType> interpolator )
    {
        this( interpolator, null, null, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );
    }
    
    /**
     * Creates a new instance of AbstractAnytimeLineMinimizer 
     * 
     * @param interpolator
     * Type of algorithm to fit data points and find an interpolated minimum
     * to the known points.
     * @param bracket
     * LineBracket bounding a local minimum.
     * @param initialGuess
     * Initial guess to the location of the local minimum.
     * @param tolerance
     * Tolerance of the minimization algorithm.
     * @param maxIterations
     * Maximum number of iterations to run the algorithm before stopping.
     */
    public AbstractAnytimeLineMinimizer(
        LineBracketInterpolator<? super EvaluatorType> interpolator,
        LineBracket bracket,
        Double initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( initialGuess, tolerance, maxIterations );
        this.setInterpolator( interpolator );
        this.setBracket( bracket );
    }
        
    @Override
    protected boolean initializeAlgorithm()
    {
        this.setValidBracket( false );
        this.setBracket( new LineBracket() );
        this.result = null;
        
        // We're initialized if we've got an initial guess AND
        // some interpolator
        return (this.getInitialGuess() != null) &&
            (this.getInterpolator() != null);
    }

    @Override
    protected boolean step()
    {
        
        boolean retval;
        
        if( !isValidBracket() )
        {
            this.setValidBracket( this.bracketingStep() );
            retval = true;
        }
        else
        {
            retval = this.sectioningStep();
        }
        
        return retval;
    }
    
    @Override
    protected void cleanupAlgorithm()
    {
    }

    public WeightedInputOutputPair<Vector, Double> minimizeAlongDirection(
        DirectionalVectorToScalarFunction function,
        Double functionValue,
        Vector gradient )
    {
        
        // We always assume that we're starting searching from 0.0
        final double x = 0.0;
        
        // If the function value is null, then evaluate it
        if( functionValue == null )
        {
            functionValue = function.evaluate( x );
        }
        
        // If the gradient is null, then stuff a null value into slope
        Double slope;
        if( gradient == null )
        {
            slope = null;
        }
        else
        {
            slope = gradient.dotProduct( function.getDirection() );
        }
        
        // Find the minimum along the direction
        this.setInitialGuess( x );
        this.setInitialGuessFunctionValue( functionValue );
        this.setInitialGuessSlope( slope );
        
        @SuppressWarnings("unchecked")
        InputOutputPair<Double, Double> lineMinimum =
            this.learn( (EvaluatorType) function );

        double scale = lineMinimum.getInput();
        Vector vectorInput = function.computeVector( scale );

        return new DefaultWeightedInputOutputPair<Vector, Double>(
            vectorInput, lineMinimum.getOutput(), scale );
    }

    
    
    public abstract boolean bracketingStep();

    public abstract boolean sectioningStep();;

    public boolean isValidBracket()
    {
        return this.validBracket;
    }

    /**
     * Setter for validBracket
     * @param validBracket
     * Flag indicating if the algorithm has already found a valid bracket on
     * a local minimum.
     */
    public void setValidBracket(
        boolean validBracket )
    {
        this.validBracket = validBracket;
    }

    public LineBracketInterpolator<? super EvaluatorType> getInterpolator()
    {
        return this.interpolator;
    }

    /**
     * Setter for interpolator
     * @param interpolator
     * Type of algorithm to fit data points and find an interpolated minimum
     * to the known points.
     */
    public void setInterpolator(
        LineBracketInterpolator<? super EvaluatorType> interpolator )
    {
        this.interpolator = interpolator;
    }

    public LineBracket getBracket()
    {
        return this.bracket;
    }

    /**
     * Setter for bracket
     * @param bracket
     * LineBracket bounding a local minimum.
     */
    public void setBracket(
        LineBracket bracket )
    {
        this.bracket = bracket;
    }

    @Override
    public void setData(
        EvaluatorType data )
    {
        super.setData( data );
    }

    @Override
    public void setInitialGuess(
        Double initialGuess )
    {
        // reset the initialGuess information
        this.setInitialGuessFunctionValue( null );
        this.setInitialGuessSlope( null );
        
        super.setInitialGuess( initialGuess );
    }
    
    /**
     * Getter for initialGuessFunctionValue
     * @return
     * Function value at the initialGuess, may be null.
     */
    public Double getInitialGuessFunctionValue()
    {
        return this.initialGuessFunctionValue;
    }

    /**
     * Setter for initialGuessFunctionValue
     * @param initialGuessFunctionValue
     * Function value at the initialGuess, may be null.
     */
    public void setInitialGuessFunctionValue(
        Double initialGuessFunctionValue )
    {
        this.initialGuessFunctionValue = initialGuessFunctionValue;
    }

    /**
     * Getter for initialGuessSlope
     * @return
     * Function slope at the initialGuess, may be null.
     */
    public Double getInitialGuessSlope()
    {
        return this.initialGuessSlope;
    }

    /**
     * Setter for initialGuessSlope
     * @param initialGuessSlope
     * Function slope at the initialGuess, may be null.
     */
    public void setInitialGuessSlope(
        Double initialGuessSlope )
    {
        this.initialGuessSlope = initialGuessSlope;
    }

}
