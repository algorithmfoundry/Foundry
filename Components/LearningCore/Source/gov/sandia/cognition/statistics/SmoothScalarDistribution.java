/*
 * File:                SmoothScalarDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 24, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * A closed-form scalar distribution that is also smooth.  That is, this
 * type of distribution has a PDF and a CDF.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface SmoothScalarDistribution
    extends ClosedFormScalarDistribution<Double>,
    ClosedFormComputableDistribution<Double>
{

    public ScalarProbabilityDensityFunction getProbabilityFunction();
    
    public SmoothCumulativeDistributionFunction getCDF();

}
