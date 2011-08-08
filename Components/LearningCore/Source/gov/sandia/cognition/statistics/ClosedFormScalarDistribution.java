/*
 * File:                ClosedFormScalarDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * Defines the functionality associated with a closed-form scalar distribution.
 * That is, a parameterized distribution that takes doubles as inputs.
 * @param <NumberType>
 * Type of Number that can be sampled from this Distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormScalarDistribution<NumberType extends Number> 
    extends ClosedFormDistribution<NumberType>,
    ScalarDistribution<NumberType>
{

    public ClosedFormCumulativeDistributionFunction<NumberType> getCDF();

}
