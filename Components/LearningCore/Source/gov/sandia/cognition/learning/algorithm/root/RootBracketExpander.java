/*
 * File:                RootBracketExpander.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.minimization.line.InputOutputSlopeTriplet;
import gov.sandia.cognition.learning.algorithm.minimization.line.LineBracket;
import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorGoldenSection;

/**
 * The root-bracketing expansion algorithm.  This function repeatedly expands
 * a root bracket about an initial guess until it finds a region where it
 * can be demonstrated that the function makes a zero-crossing (root).  The
 * algorithm can fail on functions where the function "kisses" zero, or
 * has a "notch" type look.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class RootBracketExpander
    extends AbstractAnytimeBatchLearner<Evaluator<Double,Double>, LineBracket>
    implements RootBracketer
{

    /**
     * Default scale factor on expansion, the golden ratio, {@value}
     */
    public static final double SCALE_FACTOR = LineBracketInterpolatorGoldenSection.GOLDEN_RATIO;
    
    /**
     * Default max iterations, {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100;
    
    /**
     * Default initial guess, {@value}
     */
    public static final double DEFAULT_INITIAL_GUESS = 0.0;
    
    /**
     * Bracket on the root location.
     */
    private LineBracket bracket;
    
    /**
     * Initial guess of the root's location.
     */
    private double initialGuess;
    
    /** 
     * Creates a new instance of RootBracketExpander 
     */
    public RootBracketExpander()
    {
        super( DEFAULT_MAX_ITERATIONS );
        this.setInitialGuess( DEFAULT_INITIAL_GUESS );
        this.setBracket( null );
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        Evaluator<Double,Double> f = this.data;
        double x0 = this.getInitialGuess();
        Double y0 = f.evaluate( x0 );
        InputOutputSlopeTriplet initial = new InputOutputSlopeTriplet(
            x0, y0, null );
        
        double x1 = x0 + SCALE_FACTOR;
//        double x1 = x0 + SCALE_FACTOR * y0;
        Double y1 = f.evaluate( x1 );
        InputOutputSlopeTriplet guess = new InputOutputSlopeTriplet(
            x1, y1, null );
        
        this.setBracket( new LineBracket( initial, guess, initial.clone() ) );
        
        return true;
                
    }

    @Override
    protected boolean step()
    {
        
        double x0 = this.getBracket().getLowerBound().getInput();
        double y0 = this.getBracket().getLowerBound().getOutput();
        
        double x1 = this.getBracket().getUpperBound().getInput();
        double y1 = this.getBracket().getUpperBound().getOutput();
        
        if( y0*y1 < 0.0 )
        {
            return false;
        }
        
        if( Math.abs(y0) < Math.abs(y1) )
        {
            x0 += SCALE_FACTOR * (x0-x1);
            y0 = this.data.evaluate( x0 );
            this.getBracket().getLowerBound().setInput( x0 );
            this.getBracket().getLowerBound().setOutput( y0 );
        }
        else
        {
            x1 += SCALE_FACTOR * (x1-x0);
            y1 = this.data.evaluate( x1 );
            this.getBracket().getUpperBound().setInput( x1 );
            this.getBracket().getUpperBound().setOutput( y1 );
        }

        // Keep going while the outputs are the same sign.
        // Stop when they've got opposite signs, then we definitely know
        // there's a root between them.
        return y0*y1 > 0.0;
        
    }

    @Override
    protected void cleanupAlgorithm()
    {
        
        // Make sure that a.x < b.x
        InputOutputSlopeTriplet a = this.getBracket().getLowerBound();
        InputOutputSlopeTriplet b = this.getBracket().getUpperBound();
        if( a.getInput() > b.getInput() )
        {
            this.getBracket().setLowerBound( b );
            this.getBracket().setUpperBound( a );
        }
        
        
    }

    public LineBracket getResult()
    {
        return this.getBracket();
    }

    @Override
    public boolean isResultValid()
    {
        return super.isResultValid() &&
            (this.getBracket().getLowerBound().getOutput() * this.getBracket().getUpperBound().getOutput() < 0.0);
    }
    
    /**
     * Getter for initialGuess.
     * @return
     * Initial guess of the root's location.
     */
    public double getInitialGuess()
    {
        return this.initialGuess;
    }

    public void setInitialGuess(
        double initialGuess )
    {
        this.initialGuess = initialGuess;
    }

    /**
     * Getter for bracket.
     * @return
     * Bracket on the root location.
     */
    public LineBracket getBracket()
    {
        return this.bracket;
    }

    /**
     * Setter for bracket.
     * @param bracket
     * Bracket on the root location.
     */
    protected void setBracket(
        LineBracket bracket )
    {
        this.bracket = bracket;
    }
    
}
