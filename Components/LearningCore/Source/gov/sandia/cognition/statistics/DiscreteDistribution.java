/*
 * File:                DiscreteDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import java.util.Collection;

/**
 * A Distribution with a countable domain (input) set.
 * @param <DataType> Type of data on the domain (input) set.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface DiscreteDistribution<DataType>
    extends ComputableDistribution<DataType>
{
    
    /**
     * Returns an object that allows an iteration through the domain 
     * (x-axis, independent variable) of the Distribution
     * @return
     * Collection that enumerates each value that the domain can take
     */
    public Collection<? extends DataType> getDomain();

    public ProbabilityMassFunction<DataType> getProbabilityFunction();

}
