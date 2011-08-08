/*
 * File:                ClosedFormScalarCumulativeDistributionFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * A type of closed-form CDF that's also a univariate scalar function.  Most
 * CDFs will fit this interface.
 * @param <NumberType> Type of Number to use.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormScalarCumulativeDistributionFunction<NumberType extends Number>
    extends ClosedFormCumulativeDistributionFunction<NumberType>
{
}
