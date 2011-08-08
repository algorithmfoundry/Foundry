/*
 * File:                AbstractRootFinder.java
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

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;

/**
 * Partial implementation of RootFinder.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractRootFinder
    extends AbstractAnytimeBatchLearner<Evaluator<Double,Double>, InputOutputPair<Double,Double>>
    implements RootFinder,
    MeasurablePerformanceAlgorithm
{

    /**
     * Default tolerance of the algorithm, {@value}.
     */
    public static final double DEFAULT_TOLERANCE = 1e-5;

    /**
     * Default maximum number of iterations, {@value}.
     */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;

    /**
     * Tolerance, where tolerances closer to zero are more accurate, and larger
     * tolerances are less accurate.  In any case, tolerance must be greater
     * than or equal to zero.
     */
    private double tolerance;

    /**
     * Initial guess of the root location.
     */
    private double initialGuess;

    /** 
     * Creates a new instance of AbstractRootFinder 
     */
    public AbstractRootFinder()
    {
        super( DEFAULT_MAX_ITERATIONS );
        this.setTolerance( DEFAULT_TOLERANCE );
    }

    @Override
    public AbstractRootFinder clone()
    {
        return (AbstractRootFinder) super.clone();
    }

    public double getTolerance()
    {
        return this.tolerance;
    }

    public void setTolerance(
        double tolerance )
    {
        if( tolerance < 0.0 )
        {
            throw new IllegalArgumentException( "Tolerance must be > 0.0" );
        }
        this.tolerance = tolerance;
    }

    /**
     * Returns the initial guess of the root.
     * @return
     * Initial guess of the root location.
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

    public NamedValue<Double> getPerformance()
    {
        return new DefaultNamedValue<Double>(
            "Function Value", this.getResult().getOutput() );
    }
    
}
