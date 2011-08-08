/*
 * File:                ClosedFormCumulativeDistributionFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * Functionality of a cumulative distribution function that's defined with
 * closed-form parameters.  That is, the CDF is described by a formula, not
 * empirically alone.
 * @param <DomainType> Type of Number used as the domain.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormCumulativeDistributionFunction<DomainType extends Number>
    extends ClosedFormDistribution<DomainType>,
    CumulativeDistributionFunction<DomainType>
{
}
