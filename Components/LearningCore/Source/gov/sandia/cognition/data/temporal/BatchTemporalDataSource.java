/*
 * File:                BatchTemporalDataSource.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 28, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.data.temporal;

import gov.sandia.cognition.util.Temporal;
import java.util.Date;
import java.util.List;

/**
 * Defines the interface for an offline temporal data source, which can
 * be resampled.
 * 
 * @param   <DataType> The data type. Must implement the {@code Temporal} 
 *      interface.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface BatchTemporalDataSource<DataType extends Temporal>
    extends TemporalDataSource<DataType>, List<DataType>
{
    /**
     * Gets the read channel for the data source.
     * 
     * @return  The read channel for the data source.
     */
    public SeekableTemporalDataReadChannel<DataType> readChannel();
    
    /**
     * Creates a new version of this data source that is aligned to the given
     * temporal data source using zero-order holding.
     * 
     * @param   alignTo The data source to align to.
     * @return  A new version of this data source that is zero-order hold s
     *      aligned to the given data source.
     */
    public
    BatchTemporalDataSource<DataType> zeroOrderHold(
        BatchTemporalDataSource<? extends Temporal> alignTo);
    
    /**
     * Resamples the data at the given sample period using zero-order hold.
     * 
     * @param   samplePeriod persiod, in seconds, at which to resample the data.
     * @return  Resampled data set
     */
    public BatchTemporalDataSource<DataType> resample(
        double samplePeriod);
    
    /**
     * Gets the minimum time for the dataset.
     * 
     * @return  The minimum time.
     */
    public Date getMinTime();
    
    /**
     * Gets the maximum time for the dataset.
     * 
     * @return  The maximum time.
     */
    public Date getMaxTime();
    
}
