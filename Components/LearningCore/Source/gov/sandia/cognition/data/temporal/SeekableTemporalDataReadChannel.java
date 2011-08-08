/*
 * File:                SeekableTemporalDataReadChannel.java
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

/**
 * The {@code SeekableTemporalDataReadChannel} interface extends the 
 * {@code TemporalDataReadChannel} interface to give the ability to seek around in
 * the temporal data based on a time coordinate.
 * 
 * @param   <DataType> The data that is being read in.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface SeekableTemporalDataReadChannel<DataType extends Temporal>
    extends TemporalDataReadChannel<DataType>
{
    /**
     * Seeks to the given time in the data.
     * 
     * @param   time The time to seek to.
     * @return  True if the reader 
     */
    boolean seek(
        Date time);
    
    /**
     * The minimum time coordinate on the reader.
     * 
     * @return The minimum time coordinate.
     */
    Date getMinTime();
    
    /**
     * The maximum time coordinate on the reader.
     * 
     * @return The maximum time coordinate.
     */
    Date getMaxTime();
}
