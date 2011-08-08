/*
 * File:                ClosedFormDiscreteScalarDistribution.java
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
 * A ClosedFormScalarDistribution that is also a DiscreteDistribution
 * @param <DomainType> Domain type.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormDiscreteScalarDistribution<DomainType extends Number>
    extends ClosedFormScalarDistribution<DomainType>,
    ClosedFormComputableDistribution<DomainType>,
    DiscreteDistribution<DomainType>
{    
}
