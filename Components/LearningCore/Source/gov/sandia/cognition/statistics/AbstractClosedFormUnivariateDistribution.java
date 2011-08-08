/*
 * File:                AbstractClosedFormUnivariateDistribution.java
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
 * Partial implementation of a ClosedFormUnivariateDistribution.
 * @param <NumberType> 
 * Type of Number that can be sampled from this Distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class AbstractClosedFormUnivariateDistribution<NumberType extends Number>
    extends AbstractDistribution<NumberType>
    implements ClosedFormUnivariateDistribution<NumberType>
{
    
    @Override
    @SuppressWarnings("unchecked")
    public AbstractClosedFormUnivariateDistribution<NumberType> clone()
    {
        return (AbstractClosedFormUnivariateDistribution<NumberType>) super.clone();
    }
    
}
