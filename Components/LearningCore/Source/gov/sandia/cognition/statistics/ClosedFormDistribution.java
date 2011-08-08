/*
 * File:                ClosedFormDistribution.java
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

import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Defines a distribution that is described a parameterized mathematical
 * equation.  That is, the distribution is defined in closed-form,
 * not empirically.
 * 
 * @param <DataType> Type of data on the domain of the distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormDistribution<DataType>
    extends Distribution<DataType>, Vectorizable
{    
}
