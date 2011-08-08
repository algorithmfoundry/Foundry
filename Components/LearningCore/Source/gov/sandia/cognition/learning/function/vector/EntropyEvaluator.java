/*
 * File:                EntropyEvaluator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;

/**
 * Takes a vector of inputs and computes the log base 2 entropy of the input.
 * The values of the input must sum to 1.0 (L1 norm).
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class EntropyEvaluator 
    extends AbstractCloneableSerializable
    implements Evaluator<Vector,Double>
{

    /**
     * Tolerance for asserting equal to 1.0, {@value}.
     */
    public final static double TOLERANCE = 1e-5;

    /** 
     * Creates a new instance of EntropyEvaluator 
     */
    public EntropyEvaluator()
    {
    }

    public Double evaluate(
        Vector input)
    {
        double sum = 0.0;
        final int num = input.getDimensionality();
        ArrayList<Double> values = new ArrayList<Double>( num );
        for( int i = 0; i < num; i++ )
        {
            final double v = input.getElement(i);
            sum += v;
            values.add( v );
        }

        if( Math.abs(sum-1.0) > TOLERANCE )
        {
            throw new IllegalArgumentException( "input elements must sum to 1.0" );
        }

        return UnivariateStatisticsUtil.computeEntropy(values);

    }
    
}
