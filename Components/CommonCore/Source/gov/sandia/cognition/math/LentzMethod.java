/*
 * File:                LentzMethod.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 16, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.algorithm.AbstractAnytimeAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * This class implements Lentz's method for evaluating continued fractions.
 * This is an iterative, approximate approach that has good convergence.  By
 * specifying the fraction as f(x) = b0 + a1/(b1+a1/(b2+a3/(b3+...))).
 * Each iteration, we take the (a,b) coefficients and improve the approximation
 * of f(x) and terminate after too many iterations or convergence.
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
    pages={169,171},
    url="http://www.nrbook.com/a/bookcpdf.php"
)                        
public class LentzMethod 
    extends AbstractAnytimeAlgorithm<Double>
{

    /**
     * Default value to keep denominators from equaling 0.0, default {@value}
     */
    final static double DEFAULT_MIN_DENOMINATOR = 1e-30;
    
    /**
     * Default max iterations, {@value}
     */
    final static int DEFAULT_MAX_ITERATIONS = 100;
    
    /**
     * Default convergence tolerance, {@value}
     */
    final static double DEFAULT_TOLERANCE = 3e-7;    
    
    /**
     * Tolerance of the algorithm for convergence
     */
    private double tolerance;
    
    /**
     * Value to keep denominators from equaling 0.0
     */
    private double minDenominator;
    
    /**
     * Current value of the "C" variable in Lentz's method
     */
    private double currentC;
    
    /**
     * Current value of the "D" variable in Lentz's method
     */
    private double currentD;
    
    /**
     * Value of the continued fraction, null if not valid
     */
    private Double result;
    
    /**
     * Running (intermediate) value of the fraction value
     */
    private double fractionValue;
    
    /**
     * Flag to keep going or stop
     */
    private boolean keepGoing;
    
    /** 
     * Creates a new instance of LentzMethod 
     */
    public LentzMethod()
    {
        this( DEFAULT_MAX_ITERATIONS, DEFAULT_TOLERANCE, DEFAULT_MIN_DENOMINATOR );
    }
    

    /**
     * Creates a new instance of LentzMethod 
     * 
     * @param maxIterations
     * Maximum number of iterations before stopping
     * @param tolerance
     * Tolerance of the algorithm for convergence
     * @param minDenominator
     * Value to keep denominators from equaling 0.0
     */
    public LentzMethod(
        int maxIterations,
        double tolerance,
        double minDenominator )
    {
        super( maxIterations );
        this.setTolerance(tolerance);
        this.setMinDenominator(minDenominator);
        this.keepGoing = false;
        this.result = null;
    }
    
    /**
     * Initializes Lentz's method using the given values
     * @param b0
     * Initial "b" coefficient of the continued fraction
     * @return
     * keep going flag
     */
    public boolean initializeAlgorithm(
        double b0 )
    {
        this.fireAlgorithmStarted();
        result = null;
        
        this.fractionValue = b0;
        if( Math.abs(this.fractionValue) < this.minDenominator )
        {
            this.fractionValue = this.minDenominator;
        }
        this.currentC = this.fractionValue;
        this.currentD = 0.0;
        
        
        this.iteration = 0;
        this.keepGoing = (this.iteration < this.maxIterations);
        
        return this.keepGoing;
    }
    
    /**
     * Step of Lentz's iteration
     * @param a
     * coefficient "a" from the next recursion of the continued fraction
     * @param b
     * coefficient "b" from the next recursion of the continued fraction
     * @return
     * keep going flag
     */
    public boolean iterate(
        double a,
        double b )
    {
        // If we should have already stopped, but you want us to keep going,
        // then throw an exception
        if( this.keepGoing == false )
        {
            throw new OperationNotConvergedException(
                "Trying to iterate when keepGoing is false!" );
        }
        
        this.fireStepStarted();
        
        this.iteration++;
        
        this.currentD = b + a * this.currentD;
        this.currentC = b + a / this.currentC;
        
        if( Math.abs(this.currentD) < this.minDenominator )
        {
            this.currentD = this.minDenominator;
        }
        this.currentD = 1.0 / this.currentD;

        if( Math.abs(this.currentC) < this.minDenominator )
        {
            this.currentC = this.minDenominator;
        }

        double delta = this.currentC * this.currentD;

        this.fractionValue *= delta;
        
        // We're done!
        if( Math.abs(delta-1.0) < this.tolerance )
        {
            this.result = this.getFractionValue();
            this.keepGoing = false;
        }
        
        // We've iterated too much, so stop
        if( this.iteration >= this.maxIterations )
        {
            this.keepGoing = false;
        }
        
        this.fireStepEnded();
        
        // Fire the algorithm-ended message, if appropriate
        if( this.keepGoing == false )
        {
            this.fireAlgorithmEnded();
        }
        
        return this.keepGoing;
        
    }
        
    /**
     * Getter for tolerance
     * @return
     * Tolerance of the algorithm for convergence
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Tolerance of the algorithm for convergence
     */
    public void setTolerance(
        double tolerance )
    {
        this.tolerance = tolerance;
    }

    /**
     * Getter for minDenominator
     * 
     * @return The minimum denominator.
     */
    public double getMinDenominator()
    {
        return this.minDenominator;
    }

    /**
     * Setter for minDenominator
     * @param minDenominator
     * Value to keep denominators from equaling 0.0
     */
    public void setMinDenominator(
        double minDenominator )
    {
        this.minDenominator = minDenominator;
    }

    public Double getResult()
    {
        return this.result;
    }

    public void stop()
    {
        this.keepGoing = false;
    }

    /**
     * Getter for keepGoing
     * @return
     * Flag to keep going or stop
     */
    public boolean getKeepGoing()
    {
        return this.keepGoing;
    }

    /**
     * Getter for fractionValue
     * @return
     * Running (intermediate) value of the fraction value
     */
    public double getFractionValue()
    {
        return this.fractionValue;
    }

    /**
     * Setter for fractionValue
     * @param fractionValue
     * Running (intermediate) value of the fraction value
     */
    protected void setFractionValue(
        double fractionValue )
    {
        this.fractionValue = fractionValue;
    }

}
