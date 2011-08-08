/*
 * File:                DistributionWithMean.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * A Distribution that has a well-defined mean, or first central moment.
 * @param <DataType>
 * Type of data in the domain
 * @author Kevin R. Dixon
 * @since 3.1
 */
public interface DistributionWithMean<DataType>
    extends Distribution<DataType>
{

    /**
     * Gets the arithmetic mean, or "first central moment" or "expectation",
     * of the distribution.
     * @return
     * Mean of the distribution.
     */
    public DataType getMean();
    
}
