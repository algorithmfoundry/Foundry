/*
 * File:                InvertibleCumulativeDistributionFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 7, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * A cumulative distribution function that is empirically invertible.
 * @param <NumberType> Type of Number used as the domain.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface InvertibleCumulativeDistributionFunction<NumberType extends Number>
    extends CumulativeDistributionFunction<NumberType>
{

    /**
     * Computes the inverse of the CDF for the given probability.  That is,
     * compute the value "x" such that p=CDF(x).
     * @param probability
     * Probability to invert.
     * @return
     * Inverse of the CDF for the given probability.
     */
    public NumberType inverse(
        double probability );

}
